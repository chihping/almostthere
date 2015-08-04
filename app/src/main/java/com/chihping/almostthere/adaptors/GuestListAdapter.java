package com.chihping.almostthere.adaptors;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chihping.almostthere.Constants;
import com.chihping.almostthere.R;
import com.chihping.almostthere.Utils.CircleTransform;
import com.chihping.almostthere.models.Guest;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GuestListAdapter extends BaseAdapter {

    private Query mRef;
    private int mLayout;
    private LayoutInflater mInflater;
    private List<Guest> mModels;
    private List<String> mKeys;
    private ChildEventListener guestListener;
    private ValueEventListener userListener;
    private Activity mActivity;


    /**
     * @param mRef     The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                 combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mLayout  This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                 instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity The activity containing the ListView
     */
    public GuestListAdapter(Query mRef, int mLayout, Activity activity) {
        mActivity = activity;
        this.mRef = mRef;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<>();
        mKeys = new ArrayList<>();
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView

        userListener = new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                Guest model = dataSnapshot.getValue(Guest.class);
                mModels.set(mKeys.indexOf(key), model);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };


        guestListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                String guestId = dataSnapshot.getKey();

                Guest model = new Guest();
                String key = dataSnapshot.getKey();

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    mModels.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(model);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }

                Firebase mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_USER_CHILD).child(guestId);

                mFirebaseRef.addValueEventListener(userListener);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //The existence of an user id entry indicates that they're a guest, the value is irrelevant.
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mModels.remove(index);

                //Also remove the listener

                Firebase mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_USER_CHILD).child(key);
                mFirebaseRef.removeEventListener(userListener);

                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);
                Guest newModel = mModels.get(index);
                mModels.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }


    public void cleanup() {
        // We're being destroyed, let go of all listeners
        mRef.removeEventListener(guestListener);
        for (String guestId : mKeys) {
            Firebase mFirebaseRef = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_USER_CHILD).child(guestId);
            mFirebaseRef.removeEventListener(userListener);
        }
        mModels.clear();
        mKeys.clear();
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }

        Guest model = mModels.get(i);
        // Call out to subclass to marshall this model into the provided view
        populateView(view, model);
        return view;
    }


    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The arguments correspond to the mLayout and mModelClass given to the constructor of this class.
     * <p/>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v     The view to populate
     * @param model The object containing the data used to populate the view
     */
    protected void populateView(View v, Guest model) {
        TextView etaText = (TextView) v.findViewById(R.id.txt_guest_eta);
        TextView nameText = (TextView) v.findViewById(R.id.txt_guest_name);

        ImageView profileImage = (ImageView) v.findViewById(R.id.img_guest_profile);
        String etaFormattedText = mActivity.getString(R.string.eta_unknown);
        if (model.getEta() > 0) {
            etaFormattedText = mActivity.getResources().getQuantityString(R.plurals.eta_minute, model.getEta(), model.getEta());
            etaText.setText(etaFormattedText);
        }
        etaText.setText(etaFormattedText);
        nameText.setText(model.getDisplayName());
        if (model.getProfileImageURL() != null) {
            Picasso.with(mActivity).load(Uri.parse(model.getProfileImageURL())).transform(new CircleTransform()).into(profileImage);
        } else {
            Picasso.with(mActivity).load(R.drawable.ic_contact_picture).transform(new CircleTransform()).into(profileImage);
        }
    }
}
