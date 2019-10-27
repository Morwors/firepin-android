package com.korbacorp.firepin;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.korbacorp.firepin.logged_in.LoggedInActivity;
import com.korbacorp.firepin.logged_in.util.SharedPref;
import com.korbacorp.firepin.logged_out.LoggedOutActivity;
import com.korbacorp.firepin.logged_out.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},10);

        Intent activityIntent;

        Gson gson = new Gson();
        User user = gson.fromJson(SharedPref.getStringPreference(this,"USER"), User.class);
        if(user !=null){
            if (user.getId() != null) {
                activityIntent = new Intent(this, LoggedInActivity.class);
                Log.d("USER: ", SharedPref.getStringPreference(this, "USER"));
            } else {
                activityIntent = new Intent(this, LoggedOutActivity.class);
            }
        } else {
            activityIntent = new Intent(this, LoggedOutActivity.class);
        }


        startActivity(activityIntent);
        this.finish();
    }
}
