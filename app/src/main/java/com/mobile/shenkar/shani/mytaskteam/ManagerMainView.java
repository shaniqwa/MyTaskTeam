package com.mobile.shenkar.shani.mytaskteam;

/**
 * Created by Shani on 12/14/15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("ALL")
public class ManagerMainView extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{
    String myID;
    String myRole;
    String myName;
    JSONArray m_allTasks = null;
    Spinner sort;
    SampleFragmentPagerAdapter pageAdapter;
    Integer newCounter = 0;
    ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtt_activity_main);

        myName = PreferenceManager.getDefaultSharedPreferences(this).getString("StoredName", "My task team");

        // receives values from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                myID= null;
            } else {
                myID= extras.getString("UID");
                myRole= extras.getString("role");
            }
        } else {
            myID = (String) savedInstanceState.getSerializable("UID");
        }

        //drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(myName);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //end drawer


        FloatingActionButton floating_check = (FloatingActionButton)findViewById(R.id.fab_check_task);
        floating_check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check();
            }
        });
        //floating action button add task - visible only to manager
        FloatingActionButton floating = (FloatingActionButton)findViewById(R.id.fab_add_task);

        if(this.myRole.compareTo("manager") != 0) {
            floating.hide();
            MenuItem item = navigationView.getMenu().getItem(0);
            item.setVisible(false);
        }
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        pageAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                ManagerMainView.this);
        viewPager.setAdapter(pageAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //set sort by spinner
        sort = (Spinner) findViewById(R.id.sort);
        List<String> sort_list;
        sort_list = new ArrayList<String>();
        sort_list.add("Priority");
        sort_list.add("Due Date");

        // Initializing an ArrayAdapter for "sort by" spinner
        ArrayAdapter<String> sortArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, sort_list
        );
        sortArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sort.setAdapter(sortArrayAdapter);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        SortByPriority(selectedItemView);
                        break;
                    case 1:
                        SortByDate(selectedItemView);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                SortByDate(parentView);
            }
        });

        //first load of the data from server
        try {
            reloadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void  reloadData() throws JSONException {
        m_allTasks = null;
        //get tasks from server
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json_req = new JSONObject();
                String res = "";
                try {
                    json_req.put("role", getMyRole());
                    json_req.put("id", myID);
                } catch (JSONException e) {
                    res = "-1";
                }

                try {
                    String strURL = "http://pinkladystudio.com/MyTaskTeam/get_tasks.php";
                    res = TalkToServer.PostToUrl(strURL, json_req.toString());
                } catch (Exception ex) {
                    res = "-1";
                    Log.e("error creating SignIn: ", ex.toString());
                }


                try {
                    //parse the result from server to json array
                    m_allTasks = new JSONArray(res);


                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + res + "\"");
                }
            }
        });

        thread.start();

        while (m_allTasks == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //check if there are new tasks - show notification
        if(getMyRole().compareTo("member")==0){
            JSONObject temp = null;
            JSONObject obj = null;
            if(m_allTasks!=null){
                newCounter = 0;
                for (int j = 0; j < m_allTasks.length(); j++) {
                    temp = m_allTasks.getJSONObject(j);
                    if (temp.getString("new").compareTo("0") == 0) {
                        obj = m_allTasks.getJSONObject(j);
                        newCounter++;
                    }
                }
                if(newCounter == 1){
                    Notification(newCounter, obj);
                }else{
                    obj = null;
                    Notification(newCounter, obj);
                }
            }
        }
    }


    public String getMyRole() {
        try {
            if (myRole.isEmpty()) {
                this.myRole = "manager";
            }
        }
        catch(Exception ex) {
            this.myRole = "member";
        }
        return this.myRole;
    }

    public ArrayList<JSONObject> getJSONTaskForPageNumber(int i) {
        ArrayList<JSONObject> arrObjects = new ArrayList<JSONObject>();
        try {
            if(m_allTasks != null) {
                if (i == 1) {
                    // all
                    for (int j = 0; j < m_allTasks.length(); j++) {
                        JSONObject obj = m_allTasks.getJSONObject(j);
                        if (obj.getString("status").compareTo("3") != 0) {
                            arrObjects.add(obj);
                        }
                    }
                } else if (i == 2) {
                    // done
                    for (int j = 0; j < m_allTasks.length(); j++) {
                        JSONObject obj = m_allTasks.getJSONObject(j);
                        Log.d("status", obj.getString("status"));
                        if (obj.getString("status").compareTo("3") == 0) {
                            arrObjects.add(obj);
                        }
                    }
                }
            }

            return arrObjects;
        } catch (JSONException e) {
            Log.e("err: " , e.getMessage());
            return new ArrayList<JSONObject>();
        }
    }



    public void AddTaskClicked(View v){
        Intent myIntent = new Intent(ManagerMainView.this, CreateEditTask.class);
        myIntent.putExtra("myID", myID);
        ManagerMainView.this.startActivity(myIntent);
    }
    public void ManageClicked(){
        Intent myIntent = new Intent(ManagerMainView.this, InviteMembers.class);
        myIntent.putExtra("UID", myID);
        myIntent.putExtra("name", myName);
        ManagerMainView.this.startActivity(myIntent);
    }
    public void About(){
        Intent myIntent = new Intent(ManagerMainView.this, About.class);
        ManagerMainView.this.startActivity(myIntent);
    }
    public void Settings(){
        Intent myIntent = new Intent(ManagerMainView.this, Settings.class);
        ManagerMainView.this.startActivity(myIntent);
    }


    public void Logout(){
        //clear my data
        SharedPreferences prefs = getSharedPreferences("MyTaskTeam", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit().clear();
        editor.apply();
        Intent myIntent = new Intent(ManagerMainView.this, MainActivity.class);
        ManagerMainView.this.startActivity(myIntent);
        finish();
    }
    public void check(){
        showToast("Looking for new tasks...");
        pageAdapter.refreshAll();
    }
    public void SortByDate(View v){
        pageAdapter.sortByDate();
    }
    public void SortByPriority(View v){
        pageAdapter.sortByPriority();
    }

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ManagerMainView.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            ManageClicked();
        } else if (id == R.id.nav_settings) {
            Settings();

        } else if (id == R.id.nav_logout) {
            Logout();
        } else if (id == R.id.nav_about) {
            About();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void Notification(int newTasks, JSONObject obj){
        Intent resultIntent;
        if(newTasks == 0){ return; }
        else{
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("My Task Team");
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                            if(newTasks == 1 ){
                                mBuilder.setContentText("You have received a new Task");
                                resultIntent = new Intent(this, ReportTask.class);
                                resultIntent.putExtra("task_json", obj.toString());
                                stackBuilder.addParentStack(ReportTask.class);

                            }else{
                                mBuilder.setContentText("You have received some new Tasks");
                                resultIntent = new Intent(this,ManagerMainView.class);
                                resultIntent.putExtra("UID", myID);
                                resultIntent.putExtra("role", myRole);
                                resultIntent.putExtra("name", myName);
                                stackBuilder.addParentStack(ManagerMainView.class);
                            }

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int mId = 1;
            mNotificationManager.notify(mId, mBuilder.build());

        }
    }
}



