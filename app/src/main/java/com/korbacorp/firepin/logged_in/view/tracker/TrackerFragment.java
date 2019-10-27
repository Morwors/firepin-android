package com.korbacorp.firepin.logged_in.view.tracker;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.korbacorp.firepin.R;
import com.korbacorp.firepin.logged_in.model.NotifMessage;
import com.korbacorp.firepin.logged_in.util.SharedPref;
import com.korbacorp.firepin.logged_in.view.settings.SettingsFragment;
import com.korbacorp.firepin.logged_out.model.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackerFragment extends Fragment implements LocationListener {

    Button buttonTrack, buttonOk, buttonStop;
    LinearLayout linearPingContainer;
    double lat, lng;
    LocationManager locationManager;
    String mprovider;
    User user;



    public TrackerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_tracker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView imageSettings = view.findViewById(R.id.imageSettings);
        imageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_logged_in_container, new SettingsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        buttonOk = view.findViewById(R.id.buttonOk);
        buttonTrack = view.findViewById(R.id.buttonTrack);
        buttonStop = view.findViewById(R.id.buttonStop);
        linearPingContainer = view.findViewById(R.id.linearPingContainer);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getActivity(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }


        Gson gson = new Gson();
        user = gson.fromJson(SharedPref.getStringPreference(getActivity(),"USER"), User.class);
        Log.d("USER111", user.getId().toString());

        buttonTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest("start");
//                buttonTrack.setVisibility(View.INVISIBLE);
//                linearPingContainer.setVisibility(View.VISIBLE);

            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest("ping");
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendRequest("end");
                buttonTrack.setVisibility(View.VISIBLE);
                linearPingContainer.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logged_in_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.tracker_menu_settings) {

        }

        return super.onOptionsItemSelected(item);


    }

    public void sendRequest(String method) {

        Log.d("GEO: ", ""+ lat);


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final String url = "http://192.168.0.17:3000/report.api/"+user.getId()+"/"+lat+"/"+lng;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "69")
                                .setSmallIcon(R.drawable.logorska)
                                .setContentTitle("Prijavljena Vatra")
                                .setContentText("Uspesno prijavljena vatra!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH);


// notificationId is a unique int for each notification that you must define
                        NotificationManagerCompat notificationManagerCompact = NotificationManagerCompat.from(getActivity());
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        Log.d("Hay12","DCM12");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder.setChannelId("com.korbacorp.safewalk");
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel(
                                    "com.korbacorp.safewalk",
                                    "SafeWalk",
                                    NotificationManager.IMPORTANCE_DEFAULT
                            );
                            if (notificationManager != null) {
                                notificationManager.createNotificationChannel(channel);
                            }
                        }
                        notificationManager.notify(69,builder.build());


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        try{

                            Log.d("Error.Response", error.getMessage());
                        }catch(Exception e) {
                            Log.d("Error.Response", e.getMessage());

                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userID", user.getId().toString());
                params.put("longitude", lng+"");
                params.put("latitude", lat+"");

                return params;
            }
        };
        queue.add(postRequest);

    }

    @Override
    public void onLocationChanged(Location location) {
        final Gson gson = new Gson();
        user = gson.fromJson(SharedPref.getStringPreference(getActivity(),"USER"), User.class);
        lat = location.getLatitude();
        lng = location.getLongitude();

        Log.d("GEO: ", ""+ lat);


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        final String url = "http://192.168.0.17:3000/sendUserPing.api/"+user.getId()+"/"+lat+"/"+lng;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        NotifMessage nm = gson.fromJson(response, NotifMessage.class);
                        final double latitude = nm.getLatitude();
                        Log.i("lat",nm.getLatitude()+"");
                        Log.i("lat",latitude+"");
                        final double longitude=nm.getLongitude();

                        if(latitude==0){
                            //nista
                        }else{
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                Log.d("address",addresses.toString());
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL







                                Log.i("NotifMessage","Getting message");
                                Log.i("NotifMessage",address);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default")
                                        .setSmallIcon(R.drawable.logorska)
                                        .setContentTitle("Vatra blizu vase lokacije")
                                        .setContentText("Registrovana je vatra u ulici: "+address)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                                NotificationManagerCompat notificationManagerCompact = NotificationManagerCompat.from(getActivity());
                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                Log.d("Hay12","DCM12");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    builder.setChannelId("com.korbacorp.safewalk");
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(
                                            "com.korbacorp.safewalk",
                                            "SafeWalk",
                                            NotificationManager.IMPORTANCE_DEFAULT
                                    );
                                    if (notificationManager != null) {
                                        notificationManager.createNotificationChannel(channel);
                                    }
                                }
                                notificationManager.notify(2,builder.build());
// notificationId is a unique int for each notification that you must define

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        try{

                            Log.d("Error.Response", error.getMessage());
                        }catch(Exception e) {
                            Log.d("Error.Response", e.getMessage());

                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userID", user.getId().toString());
                params.put("longitude", lng+"");
                params.put("latitude", lat+"");

                return params;
            }
        };
        queue.add(postRequest);


        Log.d("GEO: ", "LAT:LNG "+lat+" "+lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
