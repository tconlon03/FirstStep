package com.tiarnan.firststep;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tiarnan.firststep.utilities.SwipeDetector;

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
        cb_email = findViewById(R.id.checkBoxEmail);
        cb_phone = findViewById(R.id.checkBoxPhone);
        btn_comp_reg = findViewById(R.id.button_complete_registration);
        btn_comp_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRegistration();
            }
        });
        cb_email.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                use_email_contact = isChecked;
            }
        });
        cb_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                use_phone_contact = isChecked;
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
        //write to shared pref
        if (validateForm()){
            populateDataBundle();
            openConfirmationActivity();
        };
    }


    private void setUpValidation() {
        et_email.addTextChangedListener(new TextValidator() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){};

            @Override
            public void afterTextChanged(Editable s){
                String text = s.toString();
                validate(et_email, text);
            };

            public void validate(TextView textView, String text) {
                if (text.length() > 0) {
                    email_empty = false;
                } else {
                    email_empty = true;
                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    email_valid = true;
                } else {
                    email_valid = false;
                }
            }
        });
        et_phone.addTextChangedListener(new TextValidator() {
            @Override
            public void afterTextChanged(Editable s){
                String text = s.toString();
                validate(et_email, text);
            };

            public void validate(TextView textView, String text) {
                if (text.length() > 0) {
                    phone_empty = false;
                } else {
                    phone_empty = true;
                }
                if (android.util.Patterns.PHONE.matcher(text).matches() &&
                    text.substring(0,1).equals("+")) {
                    phone_valid = true;
                } else {
                    phone_valid = false;
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
        if (detectorListener.getLeftSwipe()){
            Log.i(contact_tag, "Contact Activity Right Swipe Event Detected");
            detectorListener.reset();
            completeRegistration();
        }
        detectorListener.reset();
        return true;
    }


    public boolean validateForm() {
        Toast toast;
        int phone = 1;
        int email = 2;
        int both = 3;
        if (!use_email_contact && !use_phone_contact) {
            toast = Toast.makeText(this, "Please select at least one contact method you'd like to use", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else if (use_phone_contact && use_email_contact) {
            if (!validateFields(both)){
                return false;
            }
        } else if (use_phone_contact) {
            if (!validateFields(phone)){
                return false;
            }
        } else if (use_email_contact) {
            if (!validateFields(email)){
                return false;
            }
        }
        return true;
    }

    // fields - used to indicate which fields are being used
    /* 1 - phone
     * 2 - email
     * 3 - both
    */
    public boolean validateFields(int fields) {
        Toast toast;
        int phone = 1;
        int email = 2;
        int both = 3;
        if (!email_valid) {
            if (fields == email) {
                toast = Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            if (!email_empty) {
                if (fields == phone) {
                    toast = Toast.makeText(this, "Please remove the invalid email address or enter " +
                            "a valid one and chose to be contacted by email.", Toast.LENGTH_SHORT);
                    toast.show();
                } else if (fields == both) {
                    toast = Toast.makeText(this, "Please enter a valid email address or remove it " +
                            "and chose not to be contacted by email", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return false;
            } else{
                if (fields == both) {
                    toast = Toast.makeText(this, "Please enter a valid email address or  " +
                            "chose not to be contacted by email", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                if (fields == phone){
                    if (!phone_valid) {
                        if (fields == 1) {
                            toast = Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT);
                            toast.show();
                            return false;
                        }
                    }
                }
            }
        } else {
            if (!phone_valid) {
                if (fields == phone) {
                    toast = Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT);
                    toast.show();
                    return false;
                }
                if (!phone_empty) {
                    //phone contains incorrect info
                    if (fields == email) {
                        toast = Toast.makeText(this, "Please remove the invalid phone number or enter " +
                                "a valid one and chose to be contacted by text.", Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (fields == both) {
                        toast = Toast.makeText(this, "Please enter a valid phone number or remove it " +
                                "and chose not to be contacted by text", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    return false;
                } else{
                    if (fields == both) {
                        toast = Toast.makeText(this, "Please enter a valid phone number or " +
                                "chose not to be contacted by text", Toast.LENGTH_SHORT);
                        toast.show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void openConfirmationActivity() {
        Log.i(contact_tag, "Contact Activity opening Confirmation Activity");
        populateDataBundle();
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("DATA_BUNDLE", data_bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
        data_bundle.putString(email_key, email_data);
        data_bundle.putString(phone_key, phone_data);
        data_bundle.putBoolean(use_email_key, use_email_contact);
        data_bundle.putBoolean(use_phone_key, use_phone_contact);
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
        //Boolean use_email_data = cb_email.isChecked();
        //Boolean use_phone_data = cb_phone.isChecked();
        ui_bundle.putBoolean(use_email_key, use_email_contact);
        ui_bundle.putBoolean(use_phone_key, use_phone_contact);
        outState.putBundle("DATA_BUNDLE", data_bundle );
        outState.putBundle("UI_BUNDLE", ui_bundle);
    }
}
