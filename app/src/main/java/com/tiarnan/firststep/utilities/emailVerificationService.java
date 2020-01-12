package com.tiarnan.firststep.utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tiarnan.firststep.Constants;
import com.tiarnan.firststep.HomeActivity;

public class emailVerificationService extends IntentService {

    private static final String email_service_tag = "EMAIL_SERVICE_LOGGING";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SharedPreferences sharedpreferences;

    public emailVerificationService(){
        super("emailVerificationService");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Stop service when app is closed
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(email_service_tag, "Email Service started");
        if (checkUserEmailVerified()){
            updateSharedPref();
        };
    }

    protected boolean checkUserEmailVerified(){
        while (true){
            user.reload();
            if (user.isEmailVerified()) {
                return true;
            }
            SystemClock.sleep(2000);
        }
    };

    public void updateSharedPref(){
        Log.d(email_service_tag, "Updating shared preferences");
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Constants.email_verified_key, true);
        editor.commit();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
