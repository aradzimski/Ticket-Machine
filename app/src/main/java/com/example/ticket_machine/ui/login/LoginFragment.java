package com.example.ticket_machine.ui.login;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.MainActivity;
import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.SharedPreferenceConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {
    private SharedPreferenceConfig preferenceConfig;

    private LoginViewModel loginViewModel;
    private EditText email, password;
    private Button btn_login;
    private ProgressBar loading;
    private static String URL_LOGIN;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel =
                ViewModelProviders.of(this).get(LoginViewModel.class);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final TextView textView = view.findViewById(R.id.text_events);
        loginViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        preferenceConfig = new SharedPreferenceConfig(getContext());

        URL_LOGIN = getString(R.string.URL_LOGIN);
        loading = view.findViewById(R.id.loading);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btn_login = view.findViewById(R.id.login_btn);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInputAndLogin(v);
            }
        });
        return view;
    }

    private void Login(final String email, final String password) {
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString(getString(R.string.success));
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals(getString(R.string.one))) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();
                                    String name = object.getString("name").trim();
                                    String email = object.getString("email").trim();
                                    String role = object.getString("permission_level");
                                    preferenceConfig.SaveUserRole(role); // Save user role/permission_level
                                    preferenceConfig.SaveUserId(id);

                                    Toast.makeText(getContext(),
                                            "Success Login. \nYour name : " + name
                                                    + "\nYour email : " + email
                                                    + "\nRole : " + role
                                            , Toast.LENGTH_LONG)
                                            .show();
                                    loading.setVisibility(View.GONE);
                                }
                                moveToNewActivity();
                            }
                            else {
                                Toast.makeText(getContext(),
                                        "Login failure, user or password is incorrect!"
                                        , Toast.LENGTH_LONG)
                                        .show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "Login Error" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Login Error" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
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

    public void confirmInputAndLogin(View v) {
        if (!validateEmail() | !validatePassword()) {
            return;
        }else{
            String email_input = email.getText().toString().trim();
            String password_input = password.getText().toString().trim();

            Login(email_input, password_input);
        }
    }

}

