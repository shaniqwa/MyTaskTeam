package com.mobile.shenkar.shani.mytaskteam;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shani on 1/18/16.
 */
public class InviteMembers extends AppCompatActivity {
    EditText teamName;
    String myID;
    String myRole;
    Button send;
    JSONArray m_allMembers = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receives values from previous activity
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                myID= null;
            } else {
                myID= extras.getString("UID");
                myRole= extras.getString("role");
            }
        } else {
            myID = (String) savedInstanceState.getSerializable("UID");
        }

        setContentView(R.layout.invite_members);

        teamName = (EditText)findViewById(R.id.editText);
        send = (Button)findViewById(R.id.button3);

        ArrayList<String> lst = getEmailDetails();
        String[] str = new String[lst.size()];
        str = lst.toArray(str);

//        String[] str= (String[]) .toArray();

        final MultiAutoCompleteTextView mt=(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);

        mt.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,str);

        mt.setThreshold(1);
        mt.setAdapter(adp);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] str = mt.getText().toString().split(", ");
                JSONObject obj = getJSONForEmailArray(str);
                sendInviteJsonToServer(obj);
                moveNext();
            }
        });
        GetMembersList();
    }

    private void sendInviteJsonToServer(final JSONObject json) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strURL = "http://pinkladystudio.com/MyTaskTeam/invite_members.php";
                    String ans = TalkToServer.PostToUrl(strURL, json.toString());
                    Log.i("sendInviteJsonToServer:", ans);
                } catch (Exception ex) {

                    Log.e("sendInviteJsonToServer:", ex.toString());
                }
            }
        });

        thread.start();
    }

    private JSONObject getJSONForEmailArray(String[] p_arrStr)
    {

        JSONObject json = new JSONObject();
        try {
            json.put("teamID", myID);
            json.put("teamName", teamName.getText());

            JSONArray invite = new JSONArray();

            for(int i=0; i<p_arrStr.length; i++) {
                JSONObject oneEmail =  new JSONObject();
                oneEmail.put("email",p_arrStr[i]);
                invite.put(oneEmail);
            }

            json.put("invite",invite);

        } catch (JSONException e) {
            json = new JSONObject();
        }
        return json;
    }

    private void moveNext()
    {
        //set the next activity
        //todo : go to invite member activity first to choose taem name and members. only then continue to main view
        Intent myIntent = new Intent(InviteMembers.this, ManagerMainView.class);
        myIntent.putExtra("UID", myID);
        myIntent.putExtra("role", myRole);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        InviteMembers.this.startActivity(myIntent);
        finish();
    }

    public ArrayList<String> getEmailDetails() {

        ArrayList<String> arr = new ArrayList<String>();

        Context context = this;
        ContentResolver cr = context.getContentResolver();

        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";


        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                try{
                    // names comes in hand sometimes
                    // String name = cur.getString(1);
                    String emlAddr = cur.getString(3);
                    // cont = new JSONObject();
                    // cont.put("fullname", name.toLowerCase());
//             cont.put("phone", phoneNumber);
                    // cont.put("email", emlAddr.toLowerCase());
                    // arr.put(cont);
                    arr.add(emlAddr.toLowerCase());
//          // keep unique only
//          if (emlRecsHS.add(emlAddr.toLowerCase())) {
//             emlRecs.add(emlAddr);
//          }
                } catch (Exception ex) {
                    // move next!
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return arr;
    }
    private void GetMembersList() {
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

                    List<String> memberlist ;
                    memberlist= new ArrayList<String>();
                    ArrayAdapter<String> memberAdapter;
                    memberAdapter = new ArrayAdapter<String>(InviteMembers.this, android.R.layout.simple_list_item_1, memberlist);
                    ListView MembersListView = (ListView)findViewById (R.id.listView);
                    MembersListView.setAdapter(memberAdapter);


                    for (int i=0; i<m_allMembers.length();i++){
                        JSONObject obj = m_allMembers.getJSONObject(i);
                        memberlist.add(obj.getString("memberName"));
                    }
                    memberAdapter.notifyDataSetChanged();

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + res + "\"");
                }
            }//end thread
        });
        thread.start();
    }

}