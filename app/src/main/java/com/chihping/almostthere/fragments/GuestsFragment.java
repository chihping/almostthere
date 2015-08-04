package com.chihping.almostthere.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chihping.almostthere.App;
import com.chihping.almostthere.Constants;
import com.chihping.almostthere.R;
import com.chihping.almostthere.activities.CreateEventActivity;
import com.chihping.almostthere.adaptors.GuestListAdapter;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class GuestsFragment extends ListFragment {

    private Activity mActivity;
    private Firebase mFirebaseRef;
    private String eventId;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public static GuestsFragment newInstance(String eventId) {

        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        GuestsFragment fragment = new GuestsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        eventId = getArguments().getString("eventId");
        mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_GUEST_CHILD).child(eventId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        GuestListAdapter adaptor = new GuestListAdapter(mFirebaseRef, R.layout.row_guest, mActivity);
        setListAdapter(adaptor);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        setEmptyText(getString(R.string.empty_guest));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_guest, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Firebase mFirebaseUserRef = new Firebase(Constants.FIREBASE_BASE_URL)
                .child(Constants.FIREBASE_USER_CHILD)
                .child(App.getmAuthData().getUid())
                .child(Constants.FIREBASE_EVENT_CHILD);
        if (id == R.id.action_add_guest) {
            Map<String, Object> event = new HashMap<>();
            event.put(eventId, true);
            mFirebaseUserRef.updateChildren(event);

            mFirebaseRef.child(App.getmAuthData().getUid()).setValue("true");

        } else if(id == R.id.action_remove_guest){

            mFirebaseUserRef.child(eventId).removeValue();
            mFirebaseRef.child(App.getmAuthData().getUid()).removeValue();
        }
        return super.onOptionsItemSelected(item);
    }
}
