package com.tiarnan.firststep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.tiarnan.firststep.utilities.emailVerificationService;
import com.tiarnan.firststep.utilities.runML;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HomeActivity extends AppCompatActivity implements CheckInListDialogFragment.Listener {

    private String home_tag = "HOME_ACTIVITY_LOGGING";

    private SharedPreferences sharedpreferences;
    private Boolean mFirstTime = false;
    private TextView mTextViewQuote;
    private FloatingActionButton mFabCheckin;
    private Button mButtonCheckin;
    private BottomSheetDialogFragment mCheckInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSharedPreferences(Constants.SETTINGS_PREFERENCE, 0).edit().clear().commit();
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("googlechrome://navigate?url=chrome://history")));
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
            //startML();
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
        mFabCheckin = findViewById(R.id.floatingActionButtonCheckin);
        mButtonCheckin = findViewById(R.id.ButtonCheckIn);
        if (mFirstTime){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have finished your required setup, If you want to change " +
                    "update or validate any contact methods you can do so from the settings menu")
                    .setTitle(R.string.finished_setup_text);
            AlertDialog dialog = builder.create();
        }
        Resources res = getResources();
        String[] quotes = res.getStringArray(R.array.quotes);
        int index = new Random().nextInt(quotes.length);
        mTextViewQuote.setText(quotes[index]);
        mFabCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn();
            }
        });
        mButtonCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn();
            }
        });
    }

    private void checkIn() {
        mCheckInDialog = CheckInListDialogFragment.newInstance(5);
        mCheckInDialog.show(getSupportFragmentManager(), CheckInListDialogFragment.TAG);
    }

    private void startML(){
        Log.d(home_tag, "ML Service being started");
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .build();
        // Passing params
        Data.Builder data = new Data.Builder();
        PeriodicWorkRequest mlRequest =
                new PeriodicWorkRequest.Builder(runML.class, 1, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        ContentResolver browserResolver = getContentResolver();
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Run ML", ExistingPeriodicWorkPolicy.REPLACE, mlRequest);
    }

    @Override
    public void onCheckInClicked(int position) {
        Resources res = getResources();
        String response = res.getStringArray(R.array.checkin_response)[position];
        Toast toast = Toast.makeText(this, response, Toast.LENGTH_SHORT);
        toast.show();
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = sharedpreferences.getString(Constants.checkin_array_key, "");
        Type type = new TypeToken<List<checkinObj>>(){}.getType(); List<checkinObj> students= gson.fromJson(json, type);
        ArrayList<checkinObj> checkinValues = gson.fromJson(json, type);
        if (checkinValues == null) checkinValues = new ArrayList<checkinObj>();
        fillCheckinArrayList(checkinValues, Integer.toString(position));
        String arrayAsString = gson.toJson(checkinValues);
/*
        Set<String> set = sharedpreferences.getStringSet(Constants.checkin_array_key, null);
        if (set == null) set = new HashSet<String>();

        set.addAll(listOfExistingScores);
        editor.putStringSet(Constants.checkin_array_key, set);*/
        editor.putString(Constants.checkin_array_key, arrayAsString);
        editor.commit();
        //TODO only allow one value per day
    }


    public void fillCheckinArrayList(ArrayList<checkinObj> checkInArrayList, String value){
        //TODO check dates
        if (checkInArrayList.size() > 30){
            checkInArrayList.remove(0);
        }
        checkInArrayList.add(new checkinObj(new Date(), value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.firststep_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings_menu_item) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}