package com.mobile.shenkar.shani.mytaskteam;

/**
 * Created by Shani on 12/14/15.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManagerMainView extends AppCompatActivity {
    String myID;
    String myRole;
    JSONArray m_allTasks = null;
    Spinner sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        // end receives values from previous activity

        //set layout
        setContentView(R.layout.manager_main_view);

        //floating action button add task - visible only to manager
       FloatingActionButton floating = (FloatingActionButton)findViewById(R.id.fab_add_task);

        if(this.myRole.compareTo("manager") != 0) {
            floating.hide();
        }
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                ManagerMainView.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        //set sort by spinner
        sort = (Spinner) findViewById(R.id.sort);
        List<String> sort_list;
        sort_list = new ArrayList<String>();
        sort_list.add("Priority");
        sort_list.add("Due Date");

        // Initializing an ArrayAdapter for category spinner
        ArrayAdapter<String> sortArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, sort_list
        );
        sortArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        sort.setAdapter(sortArrayAdapter);


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
                    sortJsonArray(m_allTasks);

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + res + "\"");
                }
            }
        });
        if(m_allTasks == null)
        {
            thread.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getMyRole().compareTo("manager") == 0) {
            menu.add(Menu.NONE, R.id.add_member, Menu.NONE, R.string.add_member_title);
        }
        menu.add(Menu.NONE, R.id.logout, Menu.NONE,"Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_member:
                AddMemberClicked();
                return true;
            case R.id.logout:
                Logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getMyRole()
    {
        if (myRole.isEmpty()) {
            this.myRole = "manager";
        }
        return this.myRole;
    }

    public ArrayList<JSONObject> getJSONTaskForPageNumber(int i)
    {
        ArrayList<JSONObject> arrObjects = new ArrayList<JSONObject>();
        try {
            if(m_allTasks != null) {
                sortJsonArray(m_allTasks);
                if (i == 1) {
                    // pending
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
    //sort function
    public static JSONArray sortJsonArray(JSONArray array) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsons.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                Integer lid = null;
                try {
                    lid = lhs.getInt("priority");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Integer rid = null;
                try {
                    rid = rhs.getInt("priority");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Here you could parse string id to integer and then compare.
//                Integer r = Integer.parseInt(rid);
//                Integer l = Integer.parseInt(lid);
                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }


    public void AddTaskClicked(View v){
        Intent myIntent = new Intent(ManagerMainView.this, CreateEditTask.class);
        myIntent.putExtra("myID", myID);
        ManagerMainView.this.startActivity(myIntent);
    }
    public void AddMemberClicked(){
        Intent myIntent = new Intent(ManagerMainView.this, InviteMembers.class);
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
    public void showToast(final String toast) {
        final int width = this.getWindow().getAttributes().width;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                Intent i = new Intent("android.intent.action.ALERT");
                i.putExtra("AlertBoxText", toast);
                i.putExtra("ParentWidth", width);
                startActivity(i);
            }
        });
    }

}



