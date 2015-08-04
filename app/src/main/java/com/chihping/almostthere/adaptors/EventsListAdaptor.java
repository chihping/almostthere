package com.chihping.almostthere.adaptors;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.chihping.almostthere.R;
import com.chihping.almostthere.models.Event;
import com.firebase.client.Query;

public class EventsListAdaptor extends FirebaseListAdapter<Event>{


    public EventsListAdaptor(Query mRef, Class<Event> mModelClass, int mLayout, Activity activity) {
        super(mRef, mModelClass, mLayout, activity);

    }

    @Override
    protected void populateView(View v, Event model) {

        TextView addressText = (TextView)v.findViewById(R.id.txt_event_address);
        TextView nameText = (TextView)v.findViewById(R.id.txt_event_title);

        addressText.setText(model.getAddress());
        nameText.setText(model.getName());

    }
}
