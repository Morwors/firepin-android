package com.korbacorp.firepin.logged_out.view.register;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.korbacorp.firepin.R;
import com.korbacorp.firepin.logged_out.view.login.LoginFragment;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    EditText editFirstName, editLastName, editEmail, editPassword, editPhone;
    Button buttonRegister;
    TextView textLogin;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        editEmail = view.findViewById(R.id.editRegisterEmail);
        editFirstName = view.findViewById(R.id.editRegisterFirstName);
        editLastName = view.findViewById(R.id.editRegisterLastName);
        editPassword = view.findViewById(R.id.editRegisterPassword);
        editPhone = view.findViewById(R.id.editRegisterPhone);
        buttonRegister = view.findViewById(R.id.buttonRegisterRegister);
        textLogin = view.findViewById(R.id.textRegisterLogin);

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout_logged_out_container, new LoginFragment());
                transaction.commit();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmail.getText().toString();
                String firstName = editFirstName.getText().toString();
                String lastName = editLastName.getText().toString();
                String password = editPassword.getText().toString();
                String phone = editPhone.getText().toString();


                registerUser(firstName, lastName, email, password, phone);

            }
        });

    }

    public void registerUser(final String firstName,final String lastName, final String email, final String password, final String phone) {


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        final Gson gson = new Gson();

        final String url = "http://192.168.0.17:3000/register.api";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout_logged_out_container, new LoginFragment());
                        transaction.commit();
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
                params.put("fname", firstName);
                params.put("lname", lastName);
                params.put("password", password);
                params.put("phonenu", phone);
                params.put("email", email);

                return params;
            }
        };
        queue.add(postRequest);

    }
}
