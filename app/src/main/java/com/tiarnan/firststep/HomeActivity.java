package com.tiarnan.firststep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import utilities.emailVerificationService;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private Boolean mFirstTime = false;
    private TextView mTextViewQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSharedPreferences(Constants.SETTINGS_PREFERENCE, 0).edit().clear().commit();
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        boolean emailVerified = sharedpreferences.getBoolean(Constants.email_verified_key, false);
        boolean phoneVerified = sharedpreferences.getBoolean(Constants.phone_verified_key, false);
        boolean waitingOnEmail = sharedpreferences.contains(Constants.waiting_for_email_verified_key);
        boolean waitingOnPhone = sharedpreferences.contains(Constants.waiting_for_phone_verified_key);
        if (waitingOnEmail) {
            //TODO: Delete users if verification link stale - 24hrs
            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(this, emailVerificationService.class);
                startService(intent);
            } else {
                //has been verified since app last opened
                emailVerified = true;
            }
        }
        if (emailVerified || phoneVerified) {
            if (waitingOnEmail || waitingOnPhone) mFirstTime = true;
            setupHomePage();
        } else if (waitingOnEmail || waitingOnPhone) {
                Bundle data_bundle = createDataBundle();
                Intent intent = new Intent(this, ConfirmationActivity.class);
                intent.putExtra("DATA_BUNDLE", data_bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
        } else {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private Bundle createDataBundle(){
        Bundle bundle = new Bundle();
        Map<String,?> preferences = sharedpreferences.getAll();

        Iterator it = preferences.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (pair.getValue().getClass().equals(String.class))
                bundle.putString(pair.getKey().toString(), pair.getValue().toString());
            else if (pair.getValue().getClass().equals(Long.class))
                bundle.putLong(pair.getKey().toString(), Long.valueOf(pair.getValue().toString()));
            else if (pair.getValue().getClass().equals(Boolean.class))
                bundle.putBoolean(pair.getKey().toString(), Boolean.valueOf(pair.getValue().toString()));
        }
        return bundle;
    }

    private void setupHomePage() {
        setContentView(R.layout.activity_home);
        mTextViewQuote = findViewById(R.id.textViewQuote);
        if (mFirstTime){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have finished your required setup, If you want to change " +
                    "update or validate any contact methods you can do so from the settings menu")
                    .setTitle("Congratulations - You're basically done!");
            AlertDialog dialog = builder.create();
        }
        Resources res = getResources();
        String[] quotes = res.getStringArray(R.array.quotes);
        int index = new Random().nextInt(quotes.length);
        mTextViewQuote.setText(quotes[index]);
    }

}
