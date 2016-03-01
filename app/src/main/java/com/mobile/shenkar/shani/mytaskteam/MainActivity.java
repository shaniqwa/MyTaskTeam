package com.mobile.shenkar.shani.mytaskteam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText email ;
    EditText pass;
    EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Sign up");
        setSupportActionBar(myToolbar);

        Drawable mDrawable = this.getResources().getDrawable(android.R.drawable.ic_menu_add);
        int backgroundColor = getResources().getColor(R.color.colorPrimaryDark);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY) );


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(mDrawable);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        email = (EditText)findViewById(R.id.txtEmail);
        pass = (EditText)findViewById(R.id.txtPass);
        phone = (EditText)findViewById(R.id.txtPhone);
        CheckPreviousLogins();
    }


    private void CheckPreviousLogins()
    {
        String strMyId = "-1";
        String strMyRole = "member";
        try
        {
            SharedPreferences prefs = getSharedPreferences("MyTaskTeam", MODE_PRIVATE);
            strMyId = prefs.getString("StoredUID", "-1");
            strMyRole = prefs.getString("StoredRole", "member");
        }
        catch (Exception e)
        {
            Log.e("Cant fetch uid", e.getMessage());
        }
        try {
            if (strMyId.compareTo("-1") != 0)
            {
                Intent myIntent = new Intent(MainActivity.this, ManagerMainView.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("UID", strMyId);
                myIntent.putExtra("role", strMyRole);
                MainActivity.this.startActivity(myIntent);
                finish();
            }
        }
        catch(Exception ex)
        {
            Log.e("Cant fetch uid", ex.getMessage());
        }
    }


    public void signin(View view) {
        //set the next activity
        Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        MainActivity.this.startActivity(myIntent);
    }

    /** Called when the user clicks the Next button */
    public void signup(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUID = "";

                    JSONObject json = new JSONObject();
                    try {
                        json.put("email", email.getText().toString());
                        json.put("password", pass.getText());
                        json.put("phone", phone.getText());
                    } catch (JSONException e) {
                        strUID = "-1";
                    }


                    try {
                        String strURL = "http://pinkladystudio.com/MyTaskTeam/signup.php";
                        strUID = TalkToServer.PostToUrl(strURL,json.toString());
                    } catch (Exception ex) {
                        strUID = "-1";
                        Log.e("error creating SignIn: ", ex.toString());
                    }

                    if(strUID.compareTo("-1") == 0) {
                        showToast("Oops! something went wrong.. please try again");
                    }
                    else {
//                        showToast("Logged in as user id: " + strUID);

                        SharedPreferences prefs = getSharedPreferences("MyTaskTeam", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("StoredUID", strUID);
                        editor.putString("StoredRole", "manager");
                        editor.commit();

                        //set the next activity
                        //todo : go to invite member activity first to choose taem name and members. only then continue to main view
                        Intent myIntent = new Intent(MainActivity.this, InviteMembers.class);
                        myIntent.putExtra("UID", strUID);
                        myIntent.putExtra("role", "manager");
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        MainActivity.this.startActivity(myIntent);
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
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
            }
        });
    }


}




