package com.example.ticket_machine.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.MainActivity;
import com.example.ticket_machine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;
    private EditText name, last_name, email, password, c_password;
    private Button btn_regist;
    private ProgressBar loading;
    private static String URL_REGIST;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +        //any letter
                    "(?=\\S+$)" +             //no white spaces
                    ".{4,25}" +               //at least 4 characters, max 25 characters
                    "$");
    private static final Pattern LAST_NAME_PATTERN = Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +        //any letter
                    "(?=\\S+$)" +             //no white spaces
                    ".{4,35}" +               //at least 4 characters, max 35 characters
                    "$");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registerViewModel =
                ViewModelProviders.of(this).get(RegisterViewModel.class);
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final TextView textView = view.findViewById(R.id.text_events);
        registerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        URL_REGIST = getString(R.string.URL_REGIST);
        loading = view.findViewById(R.id.loading);
        name = view.findViewById(R.id.name);
        last_name = view.findViewById(R.id.last_name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        c_password = view.findViewById(R.id.c_password);
        btn_regist = view.findViewById(R.id.register_btn);

        btn_regist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View  v){
                confirmInputAndRegister(v);
            }
        });

        return view;
    }

    private void Regist(final String name, final String last_name, final String email, final String password){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString(getString(R.string.success));

                            if(success.equals(getString(R.string.one))){
                                Toast.makeText(getContext(),"Register Success",Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.GONE);
                                moveToNewActivity();
                            }
                            else {
                                Toast.makeText(getContext(),
                                        "Register failure, provided data is incorrect!"
                                        , Toast.LENGTH_LONG)
                                        .show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Register Error" + e.toString(),Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Register Error" + error.toString(),Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name", name); // "name" name of field in PHP API
                params.put("last_name", last_name);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void moveToNewActivity () {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        ((Activity) getActivity()).overridePendingTransition(0, 0); // (0,0) it means no animation on transition
    }

    private boolean validateEmail() {
        String email_input = email.getText().toString().trim();
        if (email_input.isEmpty()) {
            email.setError("Field can't be empty!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_input).matches()) {
            email.setError("Please enter a valid email address.");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password_input = password.getText().toString().trim();
        if (password_input.isEmpty()) {
            password.setError("Field can't be empty!");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password_input).matches()) {
            password.setError("Password too weak, require 1 digit, 1 lower, 1 upper and 1 special character.");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String c_password_input = c_password.getText().toString().trim();
        String password_input = password.getText().toString().trim();
        if (c_password_input.isEmpty()) {
            c_password.setError("Field can't be empty!");
            return false;
        } else if (!c_password_input.equals(password_input)) {
            c_password.setError("Confirm password and password field is not equal.");
            return false;
        } else {
            c_password.setError(null);
            return true;
        }
    }

    private boolean validateName() {
        String name_input = name.getText().toString().trim();
        if (name_input.isEmpty()) {
            name.setError("Field can't be empty!");
            return false;
        } else if (!NAME_PATTERN.matcher(name_input).matches()) {
            name.setError("Please enter a valid first name, between 4 and 25 characters.");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String last_name_input = last_name.getText().toString().trim();
        if (last_name_input.isEmpty()) {
            last_name.setError("Field can't be empty!");
            return false;
        } else if (!LAST_NAME_PATTERN.matcher(last_name_input).matches()) {
            last_name.setError("Please enter a valid last name, between 4 and 35 characters.");
            return false;
        } else {
            last_name.setError(null);
            return true;
        }
    }

    private void confirmInputAndRegister(View v) {
        if (!validateName() | !validateLastName() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
            return;
        }else{
            String name_input = name.getText().toString().trim();
            String last_name_input = last_name.getText().toString().trim();
            String email_input = email.getText().toString().trim();
            String password_input = password.getText().toString().trim();

            Regist(name_input, last_name_input, email_input, password_input);
        }
    }

}
