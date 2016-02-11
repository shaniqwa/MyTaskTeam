package com.mobile.shenkar.shani.mytaskteam;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by Shani on 1/18/16.
 */
public class InviteMembers extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_members);
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
//					cont.put("phone", phoneNumber);
                    // cont.put("email", emlAddr.toLowerCase());
                    // arr.put(cont);
                    arr.add(emlAddr.toLowerCase());
//				// keep unique only
//				if (emlRecsHS.add(emlAddr.toLowerCase())) {
//					emlRecs.add(emlAddr);
//				}
                } catch (Exception ex) {
                    // move next!
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return arr;
    }

}
