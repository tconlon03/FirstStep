package com.example.tiarnan.firststep;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import utilities.SwipeDetector;
import utilities.formatUtilities;

import static utilities.formatUtilities.timestampToDayMonthYear;

public class NameAgeActivity extends AppCompatActivity  {

    private EditText et_first_name;
    private EditText et_surname;
    private DatePicker datePicker;
    private GestureDetector detector;
    private SwipeDetector detectorListener;
    String name_age_tag = "NAME_AGE_ACTIVITY_LOGGING";
    Bundle data_bundle;
    String first_name_key = "FIRST_NAME";
    String surname_key = "SURNAME";
    String dob_key = "DOB";
    boolean firstname_valid = false;
    boolean surname_valid = false;
    boolean age_set = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_age);

        et_first_name = findViewById(R.id.editTextFirstName);
        et_surname = findViewById(R.id.editTextSurname);
        datePicker = findViewById(R.id.datePicker);

        setUpValidation();

        detectorListener = new SwipeDetector();
        detector = new GestureDetector(this, detectorListener);

        data_bundle = new Bundle();

        long max_age = formatUtilities.getTimeMilliseconds() - 10L*31550400000L;
        datePicker.setMaxDate(max_age);
        int[] dmy = formatUtilities.timestampToDayMonthYear(max_age);
        datePicker.init(dmy[2], dmy[1], dmy[0], new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                age_set = true;
            }
        });
    }

    private void setUpValidation() {
        et_first_name.addTextChangedListener(new TextValidator(et_first_name) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0){
                    textView.setError("You must enter a first name");
                } else if (text.trim().length() == 0){
                    textView.setError("First name must not be all spaces");
                } else {
                    firstname_valid = true;
                }
            }
        });
        et_surname.addTextChangedListener(new TextValidator(et_surname) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0){
                    textView.setError("You must enter a first name");
                } else if (text.trim().length() == 0){
                    textView.setError("First name must not be all spaces");
                } else {
                    surname_valid = true;
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setUpUI(savedInstanceState);
    }

    public void setUpUI(Bundle bundle){
        int[] dmy = timestampToDayMonthYear(bundle.getLong(dob_key));
        datePicker.updateDate(dmy[2], dmy[1] , dmy[0]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        Log.i(name_age_tag, "NameAge Activity Touch Event Detected");
        detector.onTouchEvent(event);
        if (detectorListener.getRightSwipe()){
            detectorListener.reset();
            openWelcomeActivity();
        } else if (detectorListener.getLeftSwipe()){
            detectorListener.reset();
            validateFields(); // calls contact activity
        }
        detectorListener.reset();
        return true;
    }

    public void validateFields(){
        Toast toast;
        if (!firstname_valid){
            if (!surname_valid){
                toast = Toast.makeText(this, "Please enter a valid firstname and a valid surname", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else {
                toast = Toast.makeText(this, "Please enter a valid firstname", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        } else if (!surname_valid){
            toast = Toast.makeText(this, "Please enter a valid surname", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            if (!age_set){
                final boolean[] age_correct = {false};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm Age");
                builder.setMessage("Are you sure your Date of Birth is correct?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        age_set = true;
                        openContactActivity();
                    }});
                builder.setNegativeButton("No",  null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                openContactActivity();
            }
        }
    };

    public void openWelcomeActivity(){
        Log.i(name_age_tag, "Name Age Activity opening Welcome Activity");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void openContactActivity(){
        Log.i(name_age_tag, "Name Age Activity opening Contact Activity");
        Intent intent = new Intent(this, ContactActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        populateDataBundle();
        intent.putExtra("DATA_BUNDLE", data_bundle);
        startActivity(intent);
    }

    public void populateDataBundle(){
        String first_name_data = et_first_name.getText().toString();
        String surname_data = et_surname.getText().toString();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        long dob_data = formatUtilities.dateToTimestampSeconds(day, month, year);
        data_bundle.putString(first_name_key, first_name_data == null ? "" : first_name_data);
        data_bundle.putString(surname_key, surname_data == null ? "" : surname_data);
        data_bundle.putLong(dob_key, dob_data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        long dob_data = formatUtilities.dateToTimestampSeconds(day, month, year);
        outState.putLong(dob_key, dob_data);
    }
}
