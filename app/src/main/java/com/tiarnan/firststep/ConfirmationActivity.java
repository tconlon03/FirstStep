package com.tiarnan.firststep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.tiarnan.firststep.utilities.emailVerificationService;

public class ConfirmationActivity extends AppCompatActivity implements OnSendConfirmationListener {

    private String confirmation_tag = "CONFIRMATION_ACTIVITY_LOGGING";

    private Bundle data_bundle;

    private static String mName;
    private static long mDOB;
    private static String mEmail;
    private static Boolean mUseEmail;
    private static Boolean mUsePhone;
    private static String mPhone;
    private static String mFirstName;
    private static String mSurname;
    private static boolean mWaitingOnEmail;
    private static boolean mWaitingOnPhone;

    private long twenty_four_hours = 1000*60*60*24;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mVerificationId = null;

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        //set email confirmation fragment as default
        if (getIntent().hasExtra("DATA_BUNDLE")) {
            data_bundle = getIntent().getBundleExtra("DATA_BUNDLE");
            mFirstName = data_bundle.getString(Constants.first_name_key);
            mSurname = data_bundle.getString(Constants.surname_key);
            mName = mFirstName + " " + mSurname;
            mDOB = data_bundle.getLong(Constants.dob_key);
            mEmail = data_bundle.getString(Constants.email_key);
            mPhone = data_bundle.getString(Constants.phone_key);
            mUseEmail = data_bundle.getBoolean(Constants.use_email_key);
            mUsePhone = data_bundle.getBoolean(Constants.use_phone_key);
            //check if this is not the first time in this activity
            mWaitingOnEmail = data_bundle.containsKey(Constants.waiting_for_email_verified_key);
            mWaitingOnPhone = data_bundle.containsKey(Constants.waiting_for_phone_verified_key);
            if (mUseEmail == true ){
                if(mUsePhone == true){
                    EmailPhoneConfirmationFragment emailPhoneConfirmationFragment = EmailPhoneConfirmationFragment.newInstance(mName, mDOB, mEmail, mPhone);
                    this.setFragment(emailPhoneConfirmationFragment);
                } else {
                    Fragment emailConfirmationFragment = EmailConfirmationFragment.newInstance(mName, mDOB, mEmail);
                    this.setFragment(emailConfirmationFragment);
                }
            } else {
                PhoneConfirmationFragment phoneConfirmationFragment = PhoneConfirmationFragment.newInstance(mName, mDOB, mPhone);
                this.setFragment(phoneConfirmationFragment);
            }
        }
        mAuth = FirebaseAuth.getInstance();
        if (mWaitingOnEmail) mUser = mAuth.getCurrentUser();
        if (mWaitingOnPhone) mVerificationId = getVerificationId();
        writeToSharedPref();
    }

    @Override
    public void onResume(){
        super.onResume();
        // by this point the fragment onCreateView has been called
        Fragment f = getSupportFragmentManager().findFragmentByTag("CurrentFragment");
        if (mWaitingOnPhone){
            if (mUseEmail) ((EmailPhoneConfirmationFragment) f).onReceiveCode();
            else ((PhoneConfirmationFragment) f).onReceiveCode();
        }
    }

    // Replace current Fragment with the destination Fragment.
    public void setFragment(Fragment destFragment)
    {
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(R.id.confirmation_base_layout, destFragment, "CurrentFragment");

        // Commit the Fragment replace action.
        fragmentTransaction.commit();
    }

    /*
     * Send confirm email using firebase
     */
    @Override
    public void sendConfirmationEmail(){
        String email_addr = data_bundle.getString(Constants.email_key);
        String password = Integer.toString(new Random().nextInt(1000000) + 100000);
        if(mUser == null) {
            mAuth.createUserWithEmailAndPassword(email_addr, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(confirmation_tag, "createUserWithEmail:success");
                                mUser = mAuth.getCurrentUser();
                                sendFirebaseConfirmationEmailForUser(mUser);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(confirmation_tag, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(ConfirmationActivity.this, "Authentication failed : \n" +
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            sendFirebaseConfirmationEmailForUser(mUser);
        }
    }

    private void sendFirebaseConfirmationEmailForUser(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ConfirmationActivity.this, "Verification Email Sent.",
                                Toast.LENGTH_SHORT).show();
                        confirmationEmailSent();
                    }
                });
    }

    public void confirmationEmailSent(){
        Log.d(confirmation_tag, "Email sent, Service being started");
        addWaitingStatus(Constants.waiting_for_email_verified_key, Constants.email_verification_expiration_key);
        Intent intent = new Intent(this, emailVerificationService.class);
        startService(intent);
        Log.d(confirmation_tag, "Email Service started by conf activity");
    };

    @Override
    public void sendConfirmationText() {
        String phone_no = data_bundle.getString(Constants.phone_key);
        Log.i(confirmation_tag, "Confirmation Activity sending text to : " + phone_no );
        PhoneAuthProvider.getInstance().verifyPhoneNumber( phone_no, 60, TimeUnit.SECONDS,
                this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                //instant verification means this method needs to trigger updating shared preferences
                /*Fragment f = getSupportFragmentManager().findFragmentByTag("CurrentFragment");
                if (!mUseEmail){
                    ((PhoneConfirmationFragment) f).onAutoReceiveCode(credential.getSmsCode());
                } else {
                    ((EmailPhoneConfirmationFragment) f).onAutoReceiveCode(credential.getSmsCode());
                }
                phoneNumberValidated();*/
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(confirmation_tag, "code has not been sent : ", e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.i(confirmation_tag, "code has been sent : " + token);
                Toast.makeText(ConfirmationActivity.this, "Your verification code has " +
                                "been sent to you via text.",
                        Toast.LENGTH_SHORT).show();
                //now we can show the ui for entering the code
                addWaitingStatus(Constants.waiting_for_phone_verified_key, Constants.phone_verification_expiration_key);
                saveVerificationId(verificationId);
                Fragment f = getSupportFragmentManager().findFragmentByTag("CurrentFragment");
                if (!mUseEmail){
                    ((PhoneConfirmationFragment) f).onReceiveCode();
                } else {
                    ((EmailPhoneConfirmationFragment) f).onReceiveCode();
                }
            }

        });
    }

    @Override
    public void validateEnteredVerificationCode(String inputCode){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, inputCode);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(confirmation_tag, "Validate Phone Number success");
                            mUser = task.getResult().getUser();
                            phoneNumberValidated();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d(confirmation_tag, "Validate Phone Number failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Fragment f = getSupportFragmentManager().findFragmentByTag("CurrentFragment");
                                if (!mUseEmail){
                                    ((PhoneConfirmationFragment) f).onIncorrectCode();
                                } else {
                                    ((EmailPhoneConfirmationFragment) f).onIncorrectCode();
                                }
                            }
                        }
                    }
                });
    }

    public String getVerificationId(){
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        return sharedpreferences.getString("phone_verification_id", "not found");
    }

    public void saveVerificationId(String verificationId){
        mVerificationId = verificationId;
        //save to shared preferences in case app closes
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("phone_verification_id", verificationId);
        editor.commit();
    }

    //used for updating phone verified - email is done in service
    public void phoneNumberValidated(){
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(Constants.phone_verified_key, true);
        editor.commit();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            openHomeActivity();
        }
    }

    private void addWaitingStatus(String field, String expiration_field){
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(field, false);
        editor.putLong(expiration_field, new Date().getTime() + twenty_four_hours);
        editor.commit();
    }

    private void writeToSharedPref() {
        sharedpreferences = getSharedPreferences(Constants.SETTINGS_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.first_name_key, mFirstName);
        editor.putString(Constants.surname_key, mSurname);
        editor.putLong(Constants.dob_key, mDOB);
        editor.putBoolean(Constants.use_email_key, mUseEmail);
        editor.putString(Constants.email_key, mEmail);
        editor.putBoolean(Constants.use_phone_key, mUsePhone);
        editor.putString(Constants.phone_key, mPhone);
        editor.putBoolean(Constants.phone_verified_key, false);
        editor.putBoolean(Constants.email_verified_key, false);
        editor.commit();
    }

    private void openHomeActivity() {
        Log.i(confirmation_tag, "Confirmation Activity opening Home Activity");
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
