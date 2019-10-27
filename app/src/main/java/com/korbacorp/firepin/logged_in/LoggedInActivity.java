package com.korbacorp.firepin.logged_in;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.korbacorp.firepin.R;
import com.korbacorp.firepin.logged_in.view.tracker.TrackerFragment;

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);



        init();
    }

    public void init() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_logged_in_container, new TrackerFragment());
        transaction.commit();
    }
    public void checkLocation(){

    }

}
