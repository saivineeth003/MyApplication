package com.example.eshwanth.myapplication;

/**
 * Created by Eshwanth on 11/7/2015.
 */
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.Objects;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    tokenLocalStore tokenLocalStore;
    String token = "";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        tokenLocalStore = new tokenLocalStore(this);

        if(Objects.equals(tokenLocalStore.returnToken(), ""))
        {
                try {

                    InstanceID instanceID = InstanceID.getInstance(this);
                    token = instanceID.getToken("493546504308",
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.i(TAG, "GCM Registration Token: " + token);

                    tokenLocalStore.storeToken(token);

                } catch (Exception e) {
                    Log.d(TAG, "Failed to complete token refresh", e);
                }
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
