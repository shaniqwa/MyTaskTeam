package com.mobile.shenkar.shani.mytaskteam;


import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Shani on 12/14/15.
 */
// In this case, the fragment displays simple text based on the page
public class PageFragment extends ListFragment {

    protected ListView MylistView;
    protected ArrayList<JSONObject> arrObjects = null;
    public static final String ARG_PAGE = "ARG_PAGE";

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


        InteractiveArrayAdapter adapter = null;
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
    }

    // todo
    // get onclick and create intent with JSONObject from arrObjects

}