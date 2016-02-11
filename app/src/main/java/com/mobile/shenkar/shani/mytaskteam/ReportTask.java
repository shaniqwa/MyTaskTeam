package com.mobile.shenkar.shani.mytaskteam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shani on 1/18/16.
 */
public class ReportTask extends AppCompatActivity {
        private JSONObject task;
        protected TextView cat;
        protected TextView des;
        protected TextView assignee;
        protected TextView location;
        protected TextView dueDate;
        protected TextView status;
        protected TextView priority;

        private RadioButton accept;
        private RadioButton accept_waiting;
        private RadioButton accept_in_process;
        private RadioButton accept_done;
        private RadioButton reject;
    private RadioGroup radioGroupStatus;
    private Button save;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receives values from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                task = new JSONObject();
            } else {
                try {
                    task = new JSONObject(extras.getString("task_json"));
                } catch (JSONException e) {
                    task = new JSONObject();
                }
            }
        } else {
            task = new JSONObject();
        }
        setContentView(R.layout.report_task);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        cat = (TextView) findViewById(R.id.textCat);
        des = (TextView) findViewById(R.id.textDes);
        location=(TextView) findViewById(R.id.textLocation);
        priority=(TextView) findViewById(R.id.textPriority);
        dueDate=(TextView) findViewById(R.id.textDueDate);
        assignee = (TextView) findViewById(R.id.textAssignee);


        // radio button links to layout
        accept = (RadioButton)findViewById(R.id.accept);
        reject = (RadioButton)findViewById(R.id.reject);
        accept_waiting = (RadioButton)findViewById(R.id.waiting);
        accept_in_process = (RadioButton)findViewById(R.id.in_process);
        accept_done = (RadioButton)findViewById(R.id.done);



        try {
            cat.setText(task.getString("cat"));
            des.setText(task.getString("des"));
            location.setText(task.getString("location"));
            assignee.setText(task.getString("assignee"));

            dueDate.setText(task.getString("dueTime"));

            if(task.getString("priority").compareTo("1") == 0 ){
                priority.setText("Low");
            }else if (task.getString("priority").compareTo("2") == 0 ){
                priority.setText("Med");
            }else if (task.getString("priority").compareTo("3") == 0){
                priority.setText("High");
            }
            setRadioForStatus(task.getString("status"));
        } catch (JSONException e) {
            cat.setText("Error");
        }
    }

    void setRadioForStatus(String p) {
        try {
            // set all to false
            reject.setSelected(false);
            accept.setSelected(false);
            accept_waiting.setSelected(false);
            accept_in_process.setSelected(false);
            accept_done.setSelected(false);


            // set the proper one to true
            if(p.compareTo("4")==1) {
                // rej
                reject.setSelected(true);
            }
            else {
                // acc
                accept.setSelected(true);

                if(p.compareTo("0") == 1) {
                    // waiting
                    accept_waiting.setSelected(true);

                }
                else if(p.compareTo("2") == 1) {
                    // in process
                    accept_in_process.setSelected(true);

                }
                else if(p.compareTo("3") == 1) {
                    // done
                    accept_done.setSelected(true);
                }

            }
        }
        catch (Exception ex) {
        Log.e("error", ex.toString());
        }

    }
}

