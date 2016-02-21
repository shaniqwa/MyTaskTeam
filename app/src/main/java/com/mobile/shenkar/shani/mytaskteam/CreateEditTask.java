package com.mobile.shenkar.shani.mytaskteam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shani on 1/18/16.
 */
public class CreateEditTask extends AppCompatActivity implements View.OnClickListener {

    private JSONObject task;

    private RadioGroup radioGroupDate;
    private RadioGroup radioGroupPriority;
    private Button btnDisplay;


    private RadioButton priority_high;
    private RadioButton priority_medium;
    private RadioButton priority_low;

    int currPriority;

    private RadioButton date_today;
    private RadioButton date_tom;
    private RadioButton date;

    private String currAssignee;


//    TextView currDate;
    EditText des;
    Spinner assignee;
    Spinner location;
    Spinner category;

    String myID;

    JSONArray m_allMembers = null;

    private EditText dateSelection;
    private DatePickerDialog dateSelectionPickerDialog;
    private SimpleDateFormat dateFormatter;

    boolean create = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_task);

        // receives values from previous activity
            if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras.getString("task_json") == null) { //create task
                task = new JSONObject();
                myID = extras.getString("myID");
            } else { //edit task
                try {
                    create = false;
                    task = new JSONObject(extras.getString("task_json"));
                    myID = task.getString("teamID");
                } catch (JSONException e) {
                    task = new JSONObject();
                }
            }
        } else {
            task = new JSONObject();
            myID = null;
        }

//        START date picker settings
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateSelection = (EditText) findViewById(R.id.date_selecion);
        dateSelection.setInputType(InputType.TYPE_NULL);
        dateSelection.requestFocus();

        dateSelection.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        dateSelectionPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateSelection.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//        END date picker settings

//      START findViewsById
        des = (EditText) findViewById(R.id.des);
        assignee = (Spinner) findViewById(R.id.assigneeSpinner);
        location = (Spinner) findViewById(R.id.location_select);
        category = (Spinner) findViewById(R.id.catSpinner);


        priority_high = (RadioButton) findViewById(R.id.radioHigh);
        priority_medium = (RadioButton) findViewById(R.id.radioMedium);
        priority_low = (RadioButton) findViewById(R.id.radioLow);

        date_today = (RadioButton) findViewById(R.id.radioButton11);
        date_tom = (RadioButton) findViewById(R.id.radioButton14);
        date = (RadioButton) findViewById(R.id.radioButton15);
//      END findViewsById


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



        //get team members list from the server (to fill assignee spinner)
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject json_req = new JSONObject();
                String res = "";
                try {
//                    json_req.put("teamID", task.getString("teamID"));
                    json_req.put("teamID", myID);
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
            }//end thread
        });
        if(m_allMembers == null){
            thread.start();
        }

        while(m_allMembers == null)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //ASSIGNEE SPINNER
        List<String> assignee_list;
        assignee_list = new ArrayList<String>();

        // Initializing an ArrayAdapter for assignee spinner
        ArrayAdapter<String> assigneeArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, assignee_list
        );
        assigneeArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        assignee.setAdapter(assigneeArrayAdapter);

        ArrayList<JSONObject> arrObjects = new ArrayList<JSONObject>();
        try {
            if(m_allMembers != null) {
                for (int j = 0; j < m_allMembers.length(); j++) {
                    JSONObject obj = m_allMembers.getJSONObject(j);
                    assignee_list.add(obj.getString("memberName"));
                    if(!create && obj.getString("memberID").compareTo(task.getString("assignee"))==0){
                        currAssignee = obj.getString("memberName");
                    }
                    arrObjects.add(obj);
                }
                assigneeArrayAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            Log.e("err: ", e.getMessage());
        }

        if(!create){
            //START set current task values - edit task
            try {
                des.setText(task.getString("des"));

                int locationSpinnerPosition = locationArrayAdapter.getPosition(task.getString("location"));
                location.setSelection(locationSpinnerPosition);

                int categorySpinnerPosition = categoryArrayAdapter.getPosition(task.getString("cat"));
                category.setSelection(categorySpinnerPosition);

                int assigneeSpinnerPosition = assigneeArrayAdapter.getPosition(currAssignee);
                assignee.setSelection(assigneeSpinnerPosition);

                setRadioForPriority(task.getString("priority"));
                currPriority = Integer.parseInt(task.getString("priority"));

                setRadioForDate(task.getString("dueTime"));
            } catch (JSONException e) {
                des.setText("");
            }
            //END set current task values - edit task
        }else{
//            currDate.setVisibility(View.GONE);
        }
        // radio button links to layout and add event listeners
        radioGroupPriority = (RadioGroup) findViewById(R.id.radioGroupPriority);
        radioGroupPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioLow:
                        currPriority = 1;
                        break;
                    case R.id.radioMedium:
                        currPriority = 2;
                        break;
                    case R.id.radioHigh:
                        currPriority = 3;
                        break;
                }
            }
        });
        radioGroupDate = (RadioGroup) findViewById(R.id.radioGroupDate);
        radioGroupDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());

                switch(checkedId) {
                    case R.id.radioButton11: //today
                        dateSelection.setText(formattedDate);
                        break;
                    case R.id.radioButton14: //tomorrow
                        c.add(Calendar.DATE, 1);
                        formattedDate = df.format(c.getTime());
                        dateSelection.setText(formattedDate);

                        break;
                    case R.id.radioButton15: //other date
                        dateSelectionPickerDialog.show();
                        break;
                }

            }
        });


    }


    public void save_create_edit(View view) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUID = "";

                    String currAssigneeID = null;
                    for (int j = 0; j < m_allMembers.length(); j++) {
                        JSONObject obj = m_allMembers.getJSONObject(j);
                        if(obj.getString("memberName").compareTo(assignee.getSelectedItem().toString())==0){
                            currAssigneeID = obj.getString("memberID");
                        }
                    }

                    JSONObject json = new JSONObject();

                    //edit
                    if(!create){
                        try {
                            json.put("taskID", task.getString("taskID"));
                            json.put("category", category.getSelectedItem().toString());
                            json.put("des", des.getText());
                            json.put("priority", currPriority);
                            json.put("location", location.getSelectedItem().toString());
                            json.put("due_date", dateSelection.getText());
                            json.put("assignee", currAssigneeID);
                            json.put("action", "edit");
                            json.put("teamID", task.getString("teamID"));

                        } catch (JSONException e) {
                            strUID = "-1";
                        }

                    }else{
                        //create
                        try {
                            json.put("taskID", "null");
                            json.put("category", category.getSelectedItem().toString());
                            json.put("des", des.getText());
                            json.put("priority",currPriority);
                            json.put("location", location.getSelectedItem().toString());
                            json.put("due_date", dateSelection.getText());
                            json.put("assignee", currAssigneeID);
                            json.put("action", "create");
                            json.put("teamID", myID);

                        } catch (JSONException e) {
                            strUID = "-1";
                        }
                    }

                    try {
                        String strURL = "http://pinkladystudio.com/MyTaskTeam/create_edit_task.php";
                        strUID = TalkToServer.PostToUrl(strURL,json.toString());
                    } catch (Exception ex) {
                        strUID = "-1";
                        Log.e("error creating SignIn: ", ex.toString());
                    }

                    if(strUID.compareTo("-1") == 0) {
                        showToast("Oops! something went wrong.. please try again");
                    }
                    else {
                        showToast("New Task created and sent");
                        //set the next activity
                        Intent myIntent = new Intent(CreateEditTask.this, ManagerMainView.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myIntent.putExtra("UID", myID);
                        myIntent.putExtra("role", "manager");
                        CreateEditTask.this.startActivity(myIntent);
                        finish();
                    }

                } catch (Exception ex) {
                    Log.e("gg",ex.getMessage());
                }
            }
        });
        thread.start();
    }

    void setRadioForPriority(String p) {
       try{
           // set all to false
           priority_high.setChecked(false);
           priority_medium.setChecked(false);
           priority_low.setChecked(false);

           // set the proper one to true
           if(p.compareTo("1") == 0) {
               // priority = low
               priority_low.setChecked(true);
           }
           else if(p.compareTo("2") == 0) {
               // priority = medium
               priority_medium.setChecked(true);

           }
           else if(p.compareTo("3") == 0) {
               // priority = high
               priority_high.setChecked(true);

           }
       }catch (Exception ex) {
           Log.e("error", ex.toString());
       }
    }

    void setRadioForDate(String p) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        try{
            // set all to false
            date_today.setChecked(false);
            date_tom.setChecked(false);
            date.setChecked(false);

            // set the proper one to true
            if(p.compareTo(formattedDate) == 0) {
                // date = today
                date_today.setChecked(true);
                dateSelection.setText(formattedDate);
            }
            else{
                c.add(Calendar.DATE, 1);
                formattedDate = df.format(c.getTime());
                if(p.compareTo(formattedDate) == 0) {
                    // date = tomorrow
                    date_tom.setChecked(true);
                    dateSelection.setText(formattedDate);
                }else{
                    date.setChecked(true);
                    String[] separated = p.split("-");
                    int year = Integer.parseInt(separated[0]);
                    int mon = Integer.parseInt(separated[1]);
                    int day = Integer.parseInt(separated[2]);
                    c.set(year, mon-1, day );
                    dateSelectionPickerDialog.updateDate(year,mon-1,day);
                    dateSelection.setText(dateFormatter.format(c.getTime()));
                }
            }
        }catch (Exception ex) {
            Log.e("error", ex.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if(view == dateSelection) {
            dateSelectionPickerDialog.show();
        }
    }
    public void showToast(final String toast){
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(CreateEditTask.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}

