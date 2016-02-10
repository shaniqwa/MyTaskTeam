package com.mobile.shenkar.shani.mytaskteam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shani on 1/18/16.
 */
public class CreateEditTask extends AppCompatActivity {

    private JSONObject task;

    private RadioGroup radioGroupCat;
    private RadioButton radioButtonCat;
    private RadioGroup radioGroupDate;
    private RadioButton radioButtonDate;
    private RadioGroup radioGroupPriority;
    private RadioButton radioButtonPriority;
    private Button btnDisplay;


    private RadioButton priority_high;
    private RadioButton priority_medium;
    private RadioButton priority_low;


    EditText des;
    EditText assignee;
    Spinner location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_task);

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

        //links to layout
        des = (EditText)findViewById(R.id.des);
        assignee = (EditText)findViewById(R.id.autoCompleteTextAssignee);
        location = (Spinner) findViewById(R.id.location_select);

        // radio button links to layout
        priority_high = (RadioButton)findViewById(R.id.radioHigh);
        priority_medium = (RadioButton)findViewById(R.id.radioMedium);
        priority_low = (RadioButton)findViewById(R.id.radioLow);

        //add valus to location spinner
        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("30");
        list.add("204");
        list.add("247");
        list.add("2000");
        list.add("2101");

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,list
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        location.setAdapter(spinnerArrayAdapter);


        //set clicked task values - edit task
        try {
            des.setText(task.getString("des"));
            int spinnerPosition = spinnerArrayAdapter.getPosition(task.getString("location"));
            location.setSelection(spinnerPosition);
            setRadioForPriority(task.getString("priority"));
        } catch (JSONException e) {
            des.setText("");
        }
        // end set values



        addListenerOnButton();

    }

    public void addListenerOnButton() {

        radioGroupCat = (RadioGroup) findViewById(R.id.radioGroupCat);
        radioGroupDate = (RadioGroup) findViewById(R.id.radioGroupDate);
        radioGroupPriority = (RadioGroup) findViewById(R.id.radioGroupPriority);

        btnDisplay = (Button) findViewById(R.id.submit_task);

        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected Category
                int CatSelectedId = radioGroupCat.getCheckedRadioButtonId();

                // find the radio button by returned id
                radioButtonCat = (RadioButton) findViewById(CatSelectedId);

                // get selected Date
                int DateSelectedId = radioGroupCat.getCheckedRadioButtonId();

                // find the radio button by returned id
                radioButtonCat = (RadioButton) findViewById(DateSelectedId);

                // get selected Priority
                int PrioritySelectedId = radioGroupCat.getCheckedRadioButtonId();

                // find the radio button by returned id
                radioButtonCat = (RadioButton) findViewById(PrioritySelectedId);


            }

        });

    }

    public void save_create_edit(View view) {
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String strUID = "";
//
//                    JSONObject json = new JSONObject();
//                    try {
//                        json.put("taskID", "null");
//                        json.put("category", cat.getText());
//                        json.put("des", des.getText());
//                        json.put("priority", "2");
//                        json.put("location", location.getText());
//                        json.put("due_date", date.getText());
//                        json.put("assignee", "43");
//                        json.put("action", "create");
//                        json.put("teamID", "43");

//                    } catch (JSONException e) {
//                        strUID = "-1";
//                    }
//
//
//                    try {
//                        String strURL = "http://pinkladystudio.com/MyTaskTeam/create_edit_task.php";
//                        strUID = TalkToServer.PostToUrl(strURL,json.toString());
//                    } catch (Exception ex) {
//                        strUID = "-1";
//                        Log.e("error creating SignIn: ", ex.toString());
//                    }
//
//                    if(strUID.compareTo("-1") == 0) {
//                        showToast("Oops! something went wrong.. please try again");
//                    }
//                    else
//                    {
//                        showToast("Logged in as user id: " + strUID);
//
//                        SharedPreferences prefs = getSharedPreferences("MyTaskTeam", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("StoredUID", strUID);
//                        editor.putString("StoredRole", "manager");
//                        editor.commit();
//
//                        //set the next activity
//                        Intent myIntent = new Intent(MainActivity.this, ManagerMainView.class);
//                        myIntent.putExtra("UID", strUID);
//                        myIntent.putExtra("role", "manager");
//                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        MainActivity.this.startActivity(myIntent);
//                        finish();
//                    }
//
//                } catch (Exception ex) {
//                    Log.e("gg",ex.getMessage());
//                }
//            }
//        });
//        thread.start();
    }

    void setRadioForPriority(String p) {
        // set all to false
        priority_high.setSelected(false);
        priority_medium.setSelected(false);
        priority_low.setSelected(true);

        // set the proper one to true
        if(p.compareTo("1") == 1) {
            priority_high.setSelected(true);
        }
        if(p.compareTo("2") == 1){
            priority_medium.setSelected(true);
        }
        if(p.compareTo("3") == 1) {
            priority_low.setSelected(true);

        }
    }

    void setRadioForCategory(String p) {


    }

    void setRadioForDate(String p) {
    }
}

