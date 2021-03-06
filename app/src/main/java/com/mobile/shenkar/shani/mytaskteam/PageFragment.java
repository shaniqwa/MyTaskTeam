package com.mobile.shenkar.shani.mytaskteam;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shani on 12/14/15.
 */
// In this case, the fragment displays simple text based on the page
public class PageFragment extends ListFragment {

    protected ListView MylistView;
    protected ArrayList<JSONObject> arrObjects = null;
    public static final String ARG_PAGE = "ARG_PAGE";
    ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

    InteractiveArrayAdapter adapter = null;

    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }
    private void setClickEventOnList() {
        MylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    JSONObject obj = arrObjects.get(position);
                    Log.i("Clicked:",obj.getString("des"));

                    Intent intent = null;
                    if(getRole().compareTo("manager") == 0) {
                        intent = new Intent(getActivity(), CreateEditTask.class);
                    }
                    else{
                         intent = new Intent(getActivity(), ReportTask.class);
                    }
                    if(intent != null) {
                        intent.putExtra("task_json", obj.toString());
                        getActivity().startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getRole() {
        ManagerMainView t = (ManagerMainView)getActivity();
        return t.myRole;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_of_tasks, container, false);
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            MylistView = (ListView) getView().findViewById (R.id.list);
            setClickEventOnList();

            Thread FillTasksThread = new Thread(new Runnable(){
                @Override
                public void run() {
                    ManagerMainView t = (ManagerMainView)getActivity();
                    arrObjects = t.getJSONTaskForPageNumber(mPage);
                }
            });
            Thread.sleep(500);
            FillTasksThread.start();

            try {
                FillTasksThread.join(30000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {

                while(arrObjects == null){
                    // wait to fill
                    Thread.sleep(500);
                }
                adapter = new InteractiveArrayAdapter(getActivity(), arrObjects);

                if(adapter != null) {
                    setListAdapter(adapter);
                }
            } catch (Exception e) {
            Log.e("error setting adapter: ", e.getMessage());
            }
        }
        catch (Exception ex)
        {
            Log.e("error: ",ex.getMessage());
        }

//        //CHECK EVERY X TIME
//        //get time interval from users preferences. if not set, default is 1 minutes
        String time =  PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("TimeInterval", "1");
        int timeInterval = Integer.parseInt(time);
        //  schedule a runnable task every 1 minutes perform check
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i("check", "calling check");
                        check();
                    }
                });
            }
        }, 0, timeInterval, TimeUnit.MINUTES);
    }


    public void check(){
        int newCounter = 0;
        arrObjects.clear();
        arrObjects = null;
        adapter.clear();

        Thread FillTasksThread = new Thread(new Runnable(){
            @Override
            public void run() {

                ManagerMainView t = (ManagerMainView)getActivity();
                try {
                    t.reloadData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrObjects = t.getJSONTaskForPageNumber(mPage);
            }
        });
        FillTasksThread.start();

        try {
            while(arrObjects == null){
                // wait to fill
                Thread.sleep(500);
            }
            if (arrObjects != null){
                newCounter = 0;
                for (JSONObject object : arrObjects) {
                    adapter.insert(object, adapter.getCount());
                    if (object.getString("new").compareTo("0") == 0) {
                        newCounter++;
                    }
                }

                if ((getRole().compareTo("member") == 0) && (mPage == 1) && newCounter==0){
                    //if there are no new tasks set all list background color back to white
                    for (int i = 0; i < MylistView.getChildCount(); i++) {
                        View listItem = MylistView.getChildAt(i);
                        listItem.setBackgroundColor(Color.WHITE);
                    }
                }
            }
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e("error setting adapter: ", e.getMessage());
        }
        sortListbyPriority();
    }

    public void sortListbyPriority(){
        try{
            if(arrObjects != null){
            Collections.sort(arrObjects, new Comparator<JSONObject>() {
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
                    return rid.compareTo(lid);
                }
            });
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            Log.e("tag","sort");
        }

    }

    public void sortListbyDate(){
        try{
            if(arrObjects != null){
                Collections.sort(arrObjects, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        String lid = null;
                        try {
                            lid = lhs.getString("dueTime");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String rid = null;
                        try {
                            rid = rhs.getString("dueTime");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return lid.compareTo(rid);
                    }
                });
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            Log.e("tag","date");
        }

    }
}