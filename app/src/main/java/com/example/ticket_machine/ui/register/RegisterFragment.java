package com.example.ticket_machine.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class RegisterFragment extends Fragment {
    private RegisterViewModel registerViewModel;
    private EditText name, last_name, email, password, c_password;
    private Button btn_regist;
    private ProgressBar loading;
    private static String URL_REGIST;

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
                Regist();
            }
        });

        return view;
    }

    private void Regist(){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);

        final String name = this.name.getText().toString().trim();
        final String last_name = this.last_name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String password = this.password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(getContext(),"Register Success",Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.GONE);
                                moveToNewActivity();
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
                params.put("name", name); // "name" nazwa pola w API php
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

}
