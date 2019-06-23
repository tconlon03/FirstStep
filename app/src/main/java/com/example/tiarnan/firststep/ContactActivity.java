package com.example.tiarnan.firststep;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import utilities.SwipeDetector;
import utilities.formatUtilities;

public class ContactActivity extends AppCompatActivity {

    String contact_tag = "CONTACT_ACTIVITY_LOGGING";
    private EditText et_email;
    private EditText et_phone;
    private CheckBox cb_email;
    private CheckBox cb_phone;
    private Button btn_comp_reg;
    private GestureDetector detector;
    private SwipeDetector detectorListener;
    Bundle data_bundle;
    String email_key = "EMAIL";
    String phone_key = "PHONE";
    String use_email_key = "USE_EMAIL";
    String use_phone_key = "USE_PHONE";
    boolean email_valid = false;
    boolean email_empty = true;
    boolean phone_empty = true;
    boolean phone_valid = false;
    boolean use_phone_contact = false;
    boolean use_email_contact = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Log.i(contact_tag, "Contact Activity Started");
        et_email = findViewById(R.id.editTextEmail);
        et_phone = findViewById(R.id.editTextPhoneNo);
        cb_email = findViewById(R.id.checkBoxPhone);
        cb_phone = findViewById(R.id.checkBoxPhone);
        btn_comp_reg = findViewById(R.id.button_complete_registration);
        btn_comp_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRegistration();
            }
        });
        detectorListener = new SwipeDetector();
        detector = new GestureDetector(this, detectorListener);

        setUpValidation();

        if (getIntent().hasExtra("DATA_BUNDLE")) {
            data_bundle = getIntent().getBundleExtra("DATA_BUNDLE");
        }

    }

    private void completeRegistration() {
        populateDataBundle();
        //write to shared pref
        validateFields();
    }


    private void setUpValidation() {
        et_email.addTextChangedListener(new TextValidator(et_email) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0) {
                    email_empty = true;
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) ;
                {
                    email_valid = true;
                }
            }
        });
        et_phone.addTextChangedListener(new TextValidator(et_phone) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.length() == 0){
                    textView.setError("You must enter a first name");
                } else if (text.trim().length() == 0){
                    textView.setError("First name must not be all spaces");
                } else {
                    phone_valid = true;
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        data_bundle = savedInstanceState.getBundle("DATA_BUNDLE");
        setUpUI(savedInstanceState.getBundle("UI_BUNDLE"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        detector.onTouchEvent(event);
        if (detectorListener.getRightSwipe()){
            Log.i(contact_tag, "Contact Activity Right Swipe Event Detected");
            detectorListener.reset();
            openNameAgeActivity();
        }
        detectorListener.reset();
        return true;
    }


    public void validateFields(){
        Toast toast;
        //used to indicate which fields are correct and filled in
        /* 1 - phone
         * 2 - email
         * 3 - both
         */
        int correct = 0;
        if (!email_valid){
            if (!email_empty) {
                if (!phone_valid) {
                    //neither valid
                    if (!phone_empty) {
                        //both contain incorrect info
                        toast = Toast.makeText(this, "Please enter a valid email or a valid phone-number", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else {
                        //only email contains incorrect info
                        toast = Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } else {
                    //phone valid and email not
                    toast = Toast.makeText(this, "Please either remove the current emaill address or enter a valid one.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            } else {
                //email empty
                if (!phone_valid) {
                    //neither valid
                    if (phone_empty) {
                        //phone contains incorrect info
                        toast = Toast.makeText(this, "Please enter a valid email or a valid phone-number", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else {
                        //only phone contains incorrect info
                        toast = Toast.makeText(this, "Please enter a valid phone-number", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } else {
                    correct = 1;
                }
            }
        } else {
            if (!phone_valid && !phone_empty) {
                //only phone contains incorrect info
                toast = Toast.makeText(this, "Please enter a valid phone-number", Toast.LENGTH_SHORT);
                toast.show();
                return;
            } else if (phone_empty) {
                correct = 2;
            } else if (phone_valid){
                correct = 3;
            }
        }
        if (use_phone_contact){
            if (correct == 2){
                toast = Toast.makeText(this, "You've only entered a valid email. Either remove phone as a contact method or enter a valid number", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        if (use_email_contact){
            if (correct == 1){
                toast = Toast.makeText(this, "You've only entered a valid phone-number. Either remove email as a contact method or enter a valid address", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        if (!use_email_contact && !use_phone_contact){
            toast = Toast.makeText(this, "Please select any contact methods you'd like to use", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        writeToSharedPref();
    }

    private void writeToSharedPref() {

        openHomeActivity();
    }

    private void openHomeActivity() {
        Log.i(contact_tag, "Contact Activity opening Home Activity");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void openNameAgeActivity() {
        Log.i(contact_tag, "Contact Activity opening Name Age Activity");
        Intent intent = new Intent(this, NameAgeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.i(contact_tag, "Contact Activity Started");
        super.onDestroy();
    }

    public void populateDataBundle(){
        String email_data = et_email.getText().toString();
        String phone_data = et_phone.getText().toString();
        Boolean use_email_data = cb_email.isChecked();
        Boolean use_phone_data = cb_phone.isChecked();
        data_bundle.putString(email_key, email_data);
        data_bundle.putString(phone_key, phone_data);
        data_bundle.putBoolean(use_email_key, use_email_data);
        data_bundle.putBoolean(use_phone_key, use_phone_data);
    }

    public void setUpUI(Bundle bundle){
        Boolean emailcb = bundle.getBoolean(use_email_key);
        Boolean phonecb = bundle.getBoolean(use_phone_key);
        cb_email.setChecked(emailcb);
        cb_phone.setChecked(phonecb);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle ui_bundle = new Bundle();
        Boolean use_email_data = cb_email.isChecked();
        Boolean use_phone_data = cb_phone.isChecked();
        ui_bundle.putBoolean(use_email_key, use_email_data);
        ui_bundle.putBoolean(use_phone_key, use_phone_data);
        outState.putBundle("DATA_BUNDLE", data_bundle );
        outState.putBundle("UI_BUNDLE", ui_bundle);
    }
}
