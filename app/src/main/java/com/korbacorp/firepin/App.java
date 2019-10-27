package com.korbacorp.firepin;

import android.app.Application;

import com.korbacorp.firepin.logged_out.model.User;

public class App extends Application{

    User user;


        @Override
        public void onCreate() {
            super.onCreate();
            // Required initialization logic here!
        }
}
