package com.chihping.almostthere.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chihping.almostthere.App;
import com.chihping.almostthere.Constants;
import com.chihping.almostthere.R;
import com.chihping.almostthere.bus.events.EventSelected;
import com.chihping.almostthere.fragments.EventsFragment;
import com.chihping.almostthere.fragments.GuestsFragment;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventsFragment fragment = new EventsFragment();
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
        new updateETA().execute(App.getmAuthData().getUid(), null, null);
        App.getEventBus().register(this);


        Firebase connectedRef = new Firebase(Constants.FIREBASE_BASE_URL + "/.info/connected");
        final Activity mActivity = this;
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                    Toast.makeText(mActivity, "Connect to firebase", Toast.LENGTH_SHORT).show();
                    System.out.println("connected");
                } else {
                    Toast.makeText(mActivity, "Not connected to firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_event) {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void switchFragment(EventSelected eventSelected){
        if(!isFinishing()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, GuestsFragment.newInstance(eventSelected.getEventId()));
            fragmentTransaction.addToBackStack("event");
            fragmentTransaction.commitAllowingStateLoss();
        }
    }


    public static class updateETA extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            int count = params.length;
            if( count > 0){
                Firebase ref = new Firebase(Constants.FIREBASE_BASE_URL).child(Constants.FIREBASE_USER_CHILD).child(params[0]);
                //This is just to simulate changing eta
                Map<String,Object> etaMap;
                int eta = new Random().nextInt(300);
                while(true){
                    etaMap = new HashMap<>();
                    etaMap.put("eta",eta);
                    eta -= new Random().nextInt(10);
                    if(eta <= 0){
                        eta = new Random().nextInt(300);
                    }
                    ref.updateChildren(etaMap);
                    SystemClock.sleep(5000);
                }
            }
            return null;
        }
    }
}
