package com.mobile.shenkar.shani.mytaskteam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
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

        private RadioButton priority_high;
        private RadioButton priority_medium;
        private RadioButton priority_low;

        private RadioButton accept;
        private RadioButton accept_waiting;
        private RadioButton accept_in_process;
        private RadioButton accept_done;
        private RadioButton reject;

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
        assignee = (TextView) findViewById(R.id.textAssignee);
        assignee = (TextView) findViewById(R.id.textAssignee);

        // radio button links to layout
        accept = (RadioButton)findViewById(R.id.accept);
        reject = (RadioButton)findViewById(R.id.reject);

        try {
            cat.setText(task.getString("cat"));
            setRadioForStatus(task.getString("status"));
        } catch (JSONException e) {
            cat.setText("Error");
        }
    }

    void setRadioForStatus(String p) {
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
                // in proccess
                accept_in_process.setSelected(true);

            }
            else if(p.compareTo("3") == 1) {
                // done
                accept_done.setSelected(true);
            }

        }
    }

}
