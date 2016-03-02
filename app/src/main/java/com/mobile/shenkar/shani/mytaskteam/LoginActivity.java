package com.mobile.shenkar.shani.mytaskteam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email ;
    EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        myToolbar.setTitle("Login");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        email = (EditText)findViewById(R.id.txtEmail);
        pass = (EditText)findViewById(R.id.txtPass);
    }

    /** Called when the user clicks the Next button */
    public void signin(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUID = "";
                    String strROLE = "";
                    String strNAME = "";
                    JSONObject json = new JSONObject();
                    try {
                        json.put("email", email.getText().toString());
                        json.put("password", pass.getText());
                    } catch (JSONException e) {
                        strUID = "-1";
                    }


                    try {
                        String strURL = "http://pinkladystudio.com/MyTaskTeam/signin.php";
                        String ans = TalkToServer.PostToUrl(strURL, json.toString());
                        strUID = ans.split(" ")[0];
                        strROLE = ans.split(" ")[1];
                        int begin = strUID.length() + strROLE.length() + 2;
                        strNAME = ans.substring(begin,ans.length());
                    } catch (Exception ex) {
                        strUID = "-1";
                        Log.e("error creating SignIn: ", ex.toString());
                    }

                    if(strUID.compareTo("-1") == 0) {
//                        showToast("Oops! something went wrong.. please try again");
                    }
                    else{
//                        showToast("Logged in as user id: " + strUID);

                        SharedPreferences prefs = getSharedPreferences("MyTaskTeam", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("StoredUID", strUID);
                        editor.putString("StoredRole", strROLE);
                        editor.putString("StoredName", strNAME);
                        editor.commit();

                        //set the next activity
                        Intent myIntent = new Intent(LoginActivity.this, ManagerMainView.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        myIntent.putExtra("UID", strUID);
                        myIntent.putExtra("role", strROLE);
                        myIntent.putExtra("name", strNAME);
                        LoginActivity.this.startActivity(myIntent);
                        finish();
                    }

                } catch (Exception ex) {
                    Log.e("gg",ex.getMessage());
                }
            }
        });
        thread.start();
    }

    public void showToast(final String toast) {
        final int width = this.getWindow().getAttributes().width;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
                Intent i = new Intent("android.intent.action.ALERT");
                i.putExtra("AlertBoxText", toast);
                i.putExtra("ParentWidth", width);
                startActivity(i);
            }
        });
    }

}
