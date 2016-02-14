package com.mobile.shenkar.shani.mytaskteam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**

 */
public class ReportTask extends AppCompatActivity {
    private JSONObject task;
    protected TextView cat;
    protected TextView des;
    protected TextView assignee;
    protected TextView location;
    protected TextView dueDate;
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
            reject.setChecked(false);
            accept.setChecked(false);
            accept_waiting.setChecked(false);
            accept_in_process.setChecked(false);
            accept_done.setChecked(false);


            // set the proper one to true
            if(p.compareTo("4")==0) {
                // rej
                reject.setChecked(true);
                accept_waiting.setEnabled(false);
                accept_in_process.setEnabled(false);
                accept_done.setEnabled(false);
            }
            else {
                // acc
                accept.setChecked(true);

                if(p.compareTo("0") == 0) {
                    // waiting
                    accept_waiting.setChecked(true);

                }
                else if(p.compareTo("2") == 0) {
                    // in process
                    accept_in_process.setChecked(true);

                }
                else if(p.compareTo("3") == 0) {
                    // done
                    accept_done.setChecked(true);
                }

            }
        }
        catch (Exception ex) {
            Log.e("error", ex.toString());
        }

    }
}