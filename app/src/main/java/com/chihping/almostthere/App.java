package com.chihping.almostthere;

import android.app.Application;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


/**
 * Created by klai on 8/2/15.
 */
public class App extends Application {

    /* Data from the authenticated user */
    private static AuthData mAuthData;
    private static Bus bus;

    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);

        bus = new Bus(ThreadEnforcer.ANY);
    }

    public static Bus getEventBus(){
        return bus;
    }

    public static AuthData getmAuthData() {
        return mAuthData;
    }

    public void setmAuthData(AuthData mAuthData) {
        this.mAuthData = mAuthData;
    }
}
