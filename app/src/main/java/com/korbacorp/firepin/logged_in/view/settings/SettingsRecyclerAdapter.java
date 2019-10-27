package com.korbacorp.firepin.logged_in.view.settings;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.korbacorp.firepin.R;
import com.korbacorp.firepin.logged_in.model.FireLocations;
import com.korbacorp.firepin.logged_out.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.ViewHolder> {
//    private Context mContext;
    private List<FireLocations> phonesList;
    private Activity act;
    private User user;

    SettingsRecyclerAdapter(Activity act, List<FireLocations> phonesList, User user){
        this.phonesList = phonesList;
        this.act = act;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_settings, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final double latitude = phonesList.get(i).getLatitude();
        final double longitude=phonesList.get(i).getLongitude();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(act,Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("address",addresses.toString());
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            viewHolder.textPhone.setText(address);// Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }



        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteNumber(address);
            }
        });

    }

    @Override
    public int getItemCount() {
        return phonesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textPhone;
        ImageView buttonDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textPhone = itemView.findViewById(R.id.textSettingsPhone);
            buttonDelete = itemView.findViewById(R.id.imageDeletePhone);
        }
    }

    public void deleteNumber(final String number) {
        RequestQueue queue = Volley.newRequestQueue(act);
        final Gson gson = new Gson();

        final String url = "http://172.16.27.31:3000/removeNumber.api";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response: ", response);
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<FireLocations>>(){}.getType();
                        phonesList = gson.fromJson(response.toString(), listType);
                        notifyDataSetChanged();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
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
}
