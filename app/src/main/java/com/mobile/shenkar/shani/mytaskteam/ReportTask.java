package com.mobile.shenkar.shani.mytaskteam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private RadioGroup mainRadioGroupStatus;
    private RadioGroup acceptRadioGroupStatus;

   private FloatingActionButton floating ;
    public ImageView imageView  = null;
    ImageView fullScreenContainer;
    String strNewImage = null;
    boolean isImageFitToScreen;
    String currStatus;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_task);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.report_toolbar);
        myToolbar.setTitle("Report Task");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


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

        floating = (FloatingActionButton)findViewById(R.id.fab_task_done_camera);
       imageView = (ImageView) findViewById(R.id.imageView);
       floating.hide();

        /////

        //test-scale photo
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ScrollView.LayoutParams.WRAP_CONTENT));
                    imageView.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });
            //test
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
            assignee.setText(task.getString("memberName"));

            dueDate.setText(task.getString("dueTime"));

            if(task.getString("priority").compareTo("1") == 0 ){
                priority.setText("Low");
            }else if (task.getString("priority").compareTo("2") == 0 ){
                priority.setText("Normal");
            }else if (task.getString("priority").compareTo("3") == 0){
                priority.setText("Urgent");
            }
            setRadioForStatus(task.getString("status"));

            //if exists, set done image
            String img = task.getString("base64img");
            if(img != null && !img.isEmpty() ){
                String encodedImage =  task.getString("base64img");
                Bitmap decoded = imageHelper.decodeBase64(encodedImage);
                imageView.setImageBitmap(decoded);
                imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReportTask.this, FullScreenImage.class);

                        imageView.buildDrawingCache();
                        Bitmap image = imageView.getDrawingCache();

                        Bundle extras = new Bundle();
                        extras.putParcelable("imagebitmap", image);
                        intent.putExtras(extras);
                        startActivity(intent);

                    }
                });
            }
        } catch (JSONException e) {
            Log.d("SET DEFAULT: ","error setting task values");
        }

        // status radio group links to layout and add event listeners
        acceptRadioGroupStatus = (RadioGroup) findViewById(R.id.radioGroupAcceptStatus);
        acceptRadioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.waiting:
                        accept.setChecked(true);
                        floating.hide();
                        currStatus = "1";
                        break;
                    case R.id.in_process:
                        floating.hide();
                        accept.setChecked(true);
                        currStatus = "2";
                        break;
                    case R.id.done:
                        accept.setChecked(true);
                        currStatus = "3";
                        floating.show();
                        break;
                }
            }
        });

        mainRadioGroupStatus = (RadioGroup) findViewById(R.id.radioGroupAcceptReject);
        mainRadioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.reject:
                        accept_waiting.setEnabled(false);
                        accept_in_process.setEnabled(false);
                        accept_done.setEnabled(false);
                        floating.hide();
                        currStatus = "4";
                        break;
                    case R.id.accept:
                        //enable other accept status (in process/done)
                        accept_waiting.setEnabled(true);
                        accept_in_process.setEnabled(true);
                        accept_done.setEnabled(true);
                        //accept default is accept_waiting
                        accept_waiting.setChecked(true);
                        //set current status
                        currStatus = "1";
                        break;
                }
            }
        });

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
            switch (p){
                case "0": //no reply yet
                    accept_waiting.setEnabled(false);
                    accept_in_process.setEnabled(false);
                    accept_done.setEnabled(false);
                    break;
                case "1":   //accept - waiting
                    accept.setChecked(true);
                    accept_waiting.setChecked(true);
                    break;
                case "2": //accept - in process
                    accept.setChecked(true);
                    accept_in_process.setChecked(true);
                    break;
                case "3": //accept - done
                    accept.setChecked(true);
                    accept_done.setChecked(true);
                    floating.show();
                    break;
                case "4":  //reject
                    reject.setChecked(true);
                    accept_waiting.setEnabled(false);
                    accept_in_process.setEnabled(false);
                    accept_done.setEnabled(false);
                    break;
            }
        }
        catch (Exception ex) {
            Log.e("error", ex.toString());
        }
    }
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap m_bit;
    // fire camera intent
    public void dispatchTakePictureIntent(View v) {

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        catch(Exception ex){}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                try {
                    m_bit = (Bitmap) extras.get("data");
                } catch (Exception e) {
                    m_bit = null;
                }

                if (m_bit != null) {
                    // handle bitmap
                    strNewImage = imageHelper.encodeTobase64(m_bit);

                    imageView.setImageBitmap(m_bit);
                    imageView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ReportTask.this, FullScreenImage.class);

                            imageView.buildDrawingCache();
                            Bitmap image = imageView.getDrawingCache();

                            Bundle extras = new Bundle();
                            extras.putParcelable("imagebitmap", m_bit);
                            intent.putExtras(extras);
                            startActivity(intent);

                        }
                    });

                }


            }
        }
        catch(Exception ex){}
    }


    public void save_report_task(View view) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUID = "";
                    JSONObject json = new JSONObject();

                        try {
                            json.put("taskID", task.getString("taskID"));
                            json.put("status", currStatus);
                            if(strNewImage != null){
                                json.put("image", strNewImage);
                            }

                        } catch (JSONException e) {
                            strUID = "-1";
                        }

                    try {
                        String strURL = "http://pinkladystudio.com/MyTaskTeam/report_task_status.php";
                        strUID = TalkToServer.PostToUrl(strURL,json.toString());
                    } catch (Exception ex) {
                        strUID = "-1";
                        Log.e("error changing status: ", ex.toString());
                        showToast("Error Saving Task Status: Please try again");
                    }

                    if(strUID.compareTo("-1") == 0) {
                        showToast("Oops! something went wrong.. please try again");
                    }
                    else {
                        String statusIs = null;
                        switch (currStatus){
                            case "0" :
                                statusIs = "No replay";
                                break;
                            case "1" :
                                statusIs = "Waiting";
                                break;
                            case "2" :
                                statusIs = "In Process";
                                break;
                            case "3" :
                                statusIs = "Done";
                                break;
                            case "4" :
                                statusIs = "Reject";
                                break;
                        }
                        showToast("Status is now " + statusIs);
                        //set the next activity
                        Intent myIntent = new Intent(ReportTask.this, ManagerMainView.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myIntent.putExtra("UID", task.getString("assignee"));
                        myIntent.putExtra("role", "member");
                        ReportTask.this.startActivity(myIntent);
                        finish();
                    }

                } catch (Exception ex) {
                    Log.e("gg",ex.getMessage());
                }
            }
        });
        thread.start();
    }
    public void showToast(final String toast){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ReportTask.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }
}