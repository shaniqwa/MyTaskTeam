package com.mobile.shenkar.shani.mytaskteam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * Created by Shani on 12/14/15.
 */
public class CreateTeam  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_members);
    }


    /** Called when the user clicks the Send Invitations button */
    public void sendInvitations(View view) {
        // Do something in response to button

        //set the next activity
        Intent myIntent = new Intent(CreateTeam.this, ManagerMainView.class);
        CreateTeam.this.startActivity(myIntent);
    }
}
