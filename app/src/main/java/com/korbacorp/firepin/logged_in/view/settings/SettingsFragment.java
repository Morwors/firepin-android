package com.korbacorp.firepin.logged_in.view.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.korbacorp.firepin.R;
import com.korbacorp.firepin.logged_in.model.FireLocations;
import com.korbacorp.firepin.logged_in.util.SharedPref;
import com.korbacorp.firepin.logged_out.model.User;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingsFragment extends Fragment {

    User user;
    RecyclerView phoneRecycler;
    List<FireLocations> fireLocationsList = new ArrayList<>();
    SettingsRecyclerAdapter adapter;
    ImageView imageAddPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneRecycler = view.findViewById(R.id.settings_recycler);
        imageAddPhone = view.findViewById(R.id.add_phone_number);
        imageAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNumberDialog();
            }
        });
        adapter = new SettingsRecyclerAdapter(getActivity(), fireLocationsList, user);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        phoneRecycler.setLayoutManager(mLayoutManager);
        phoneRecycler.setItemAnimator(new DefaultItemAnimator());
        phoneRecycler.setAdapter(adapter);
        Gson gson = new Gson();
        user = gson.fromJson(SharedPref.getStringPreference(getActivity(),"USER"), User.class);
        getNumbers();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void getNumbers() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final String url = "http://192.168.0.69:3000/firelocation.api/";

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<FireLocations>>(){}.getType();
                        fireLocationsList = gson.fromJson(response.toString(), listType);
                        adapter = new SettingsRecyclerAdapter(getActivity(), fireLocationsList, user);
                        phoneRecycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Log.d("123", fireLocationsList.size()+"");


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{

                            Log.d("Error.Response", error.getMessage());
                        }catch(Exception e) {
                            Log.d("Error.Response", e.getMessage());

                        }
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("userID", user.getId().toString());

                return params;
            }
        };

        queue.add(getRequest);

    }

    public void showAddNumberDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_add_number, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                addNumber(editText.getText().toString());
                dialogBuilder.dismiss();

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.settings_menu_add_number) {
            showAddNumberDialog();
        }

        return super.onOptionsItemSelected(item);


    }

    public void addNumber(final String number) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        final String url = "http://172.16.27.31:3000/addnu.api";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response: ", response);
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<FireLocations>>(){}.getType();
                        fireLocationsList = gson.fromJson(response.toString(), listType);
                        adapter = new SettingsRecyclerAdapter(getActivity(), fireLocationsList, user);
                        phoneRecycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

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
                params.put("phoneNumber", number);
                params.put("userID", user.getId().toString());

                return params;
            }
        };
        queue.add(postRequest);

    }

    //getNumbers.api
    //addnu.api

}
