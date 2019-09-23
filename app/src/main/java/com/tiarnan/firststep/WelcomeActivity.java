package com.tiarnan.firststep;

import android.content.Intent;

import com.tiarnan.firststep.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import utilities.SwipeDetector;

public class WelcomeActivity extends AppCompatActivity {

    //debug tag
    String main_tag = "MAIN_ACTIVITY_LOGGING";
    //declare view components
    private FloatingActionButton fab_next;
    private TextView tv_welcome_msg;
    GestureDetector detector;
    SwipeDetector detectorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(main_tag, "Starting Main Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_welcome_msg = findViewById(R.id.textViewWelcomeMsg);
        fab_next = findViewById(R.id.floatingActionButtonNext);
        detectorListener = new SwipeDetector();
        detector = new GestureDetector(this, detectorListener);
        tv_welcome_msg.setText("Welcome to First Step");
        fab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNameAgeActivity();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(main_tag, "Welcome Activity Touch Event Detected");
        detector.onTouchEvent(event);
        if (detectorListener.getLeftSwipe()){
            detectorListener.reset();
            openNameAgeActivity();
        }
        detectorListener.reset();
        return true;
    }

    public void openNameAgeActivity(){
        Log.d(main_tag, "Welcome Activity opening NameAge Activity");
        Intent intent = new Intent(this, NameAgeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
