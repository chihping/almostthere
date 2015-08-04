package com.chihping.almostthere.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chihping.almostthere.App;
import com.chihping.almostthere.Constants;
import com.chihping.almostthere.R;
import com.chihping.almostthere.adaptors.EventsListAdaptor;
import com.chihping.almostthere.bus.events.EventSelected;
import com.chihping.almostthere.models.Event;
import com.firebase.client.Firebase;

public class EventsFragment extends ListFragment {

    private Activity mActivity;
    private Firebase mFirebaseRef;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_EVENT_CHILD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        EventsListAdaptor adaptor = new EventsListAdaptor(mFirebaseRef, Event.class, R.layout.row_event, mActivity);
        setListAdapter(adaptor);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        setEmptyText(getString(R.string.empty_event));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (isAdded()) {
            String eventKey = ((EventsListAdaptor) getListAdapter()).getKey(position);
            App.getEventBus().post(new EventSelected(eventKey));
        }
    }

}
