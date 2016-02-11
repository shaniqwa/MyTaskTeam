package com.mobile.shenkar.shani.mytaskteam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shani on 1/18/16.
 */
public class CreateEditTask extends AppCompatActivity {

    private JSONObject task;

    private RadioGroup radioGroupDate;
    private RadioButton radioButtonDate;
    private RadioGroup radioGroupPriority;
    private RadioButton radioButtonPriority;
    private Button btnDisplay;


    private RadioButton priority_high;
    private RadioButton priority_medium;
    private RadioButton priority_low;


    EditText des;
    Spinner assignee;
    Spinner location;
    Spinner category;

    JSONArray m_allMembers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_task);

        // receives values from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
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
        des = (EditText) findViewById(R.id.des);
        assignee = (Spinner) findViewById(R.id.assigneeSpinner);
        location = (Spinner) findViewById(R.id.location_select);
        category = (Spinner) findViewById(R.id.catSpinner);

        // radio button links to layout
        priority_high = (RadioButton) findViewById(R.id.radioHigh);
        priority_medium = (RadioButton) findViewById(R.id.radioMedium);
        priority_low = (RadioButton) findViewById(R.id.radioLow);

        //LOCATION SPINNER
        List<String> location_list;
        location_list = new ArrayList<String>();
        location_list.add("30");
        location_list.add("204");
        location_list.add("247");
        location_list.add("2000");
        location_list.add("2101");

        // Initializing an ArrayAdapter for location spinner
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, location_list
        );
        locationArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        location.setAdapter(locationArrayAdapter);



        //CATEGORY SPINNER
        List<String> category_list;
        category_list = new ArrayList<String>();
        category_list.add("General");
        category_list.add("Cleaning");
        category_list.add("Electricity");
        category_list.add("Computer");
        category_list.add("Other");

        // Initializing an ArrayAdapter for category spinner
        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, category_list
        );
        categoryArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        category.setAdapter(categoryArrayAdapter);



        //ASSIGNEE SPINNER
        //get team members list from the server
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json_req = new JSONObject();
                String res = "";
                try {
                    json_req.put("teamID", "43");
                } catch (JSONException e) {
                    res = "-1";
                }

                try {
                    String strURL = "http://pinkladystudio.com/MyTaskTeam/get_members.php";
                    res = TalkToServer.PostToUrl(strURL, json_req.toString());
                } catch (Exception ex) {
                    res = "-1";
                    Log.e("error post to server: ", ex.toString());
                }


                try {
                    //parse the result from server to json array
                    m_allMembers = new JSONArray(res);

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + res + "\"");
                }
            }
        });
        if(m_allMembers == null)
        {
            thread.start();
        }


        //set current task values - edit task
        try {
            des.setText(task.getString("des"));

            int locationSpinnerPosition = locationArrayAdapter.getPosition(task.getString("location"));
            location.setSelection(locationSpinnerPosition);

            int categorySpinnerPosition = categoryArrayAdapter.getPosition(task.getString("cat"));
            category.setSelection(categorySpinnerPosition);

            setRadioForPriority(task.getString("priority"));
        } catch (JSONException e) {
            des.setText("");
        }
        // end set current task values - edit task


        addListenerOnButton();

    }

    public void addListenerOnButton() {

        radioGroupDate = (RadioGroup) findViewById(R.id.radioGroupDate);
        radioGroupPriority = (RadioGroup) findViewById(R.id.radioGroupPriority);

        btnDisplay = (Button) findViewById(R.id.submit_task);

        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                // get selected Category
//                int CatSelectedId = radioGroupCat.getCheckedRadioButtonId();
//
//                // find the radio button by returned id
//                radioButtonCat = (RadioButton) findViewById(CatSelectedId);
//
//                // get selected Date
//                int DateSelectedId = radioGroupCat.getCheckedRadioButtonId();
//
//                // find the radio button by returned id
//                radioButtonCat = (RadioButton) findViewById(DateSelectedId);
//
//                // get selected Priority
//                int PrioritySelectedId = radioGroupCat.getCheckedRadioButtonId();
//
//                // find the radio button by returned id
//                radioButtonCat = (RadioButton) findViewById(PrioritySelectedId);


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
//        priority_high.setSelected(false);
//        priority_medium.setSelected(false);
//        priority_low.setSelected(true);
//
//        // set the proper one to true
//        if(p.compareTo("1") == 1) {
//            priority_high.setSelected(true);
//        }
//        if(p.compareTo("2") == 1){
//            priority_medium.setSelected(true);
//        }
//        if(p.compareTo("3") == 1) {
//            priority_low.setSelected(true);
//
//        }
//    }
//
//    void setRadioForCategory(String p) {
//
//
//    }
//
//    void setRadioForDate(String p) {
//    }
    }
}

