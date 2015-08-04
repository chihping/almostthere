package com.chihping.almostthere.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.chihping.almostthere.Constants;
import com.chihping.almostthere.R;
import com.chihping.almostthere.models.Event;
import com.firebase.client.Firebase;

public class CreateEventActivity extends AppCompatActivity {

    EditText mEventName;
    EditText mEventAddress;
    Firebase mFirebaseRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_EVENT_CHILD);

        mEventName = (EditText) findViewById(R.id.txt_event_name);
        mEventAddress = (EditText) findViewById(R.id.txt_event_address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Event event = new Event();
            event.setName(mEventName.getText().toString().trim());
            event.setAddress(mEventAddress.getText().toString().trim());
            mFirebaseRef.push().setValue(event);
            finish();
            return true;
        } else if (id == R.id.action_cancel) {
            finish();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
