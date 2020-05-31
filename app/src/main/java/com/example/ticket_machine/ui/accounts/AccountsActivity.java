package com.example.ticket_machine.ui.accounts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.Ticket;
import com.example.ticket_machine.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountsActivity extends AppCompatActivity {
    private static String URL_GETUSER;
    private static String URL_UPDATEUSERPERMISSION;
    private ProgressBar loading;
    private EditText email;
    private Button btn_search;
    private User mUser;

    private RadioGroup radio_group_role;
    private RadioButton radioButton;
    private TextView text_view_user;
    private Button btn_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        URL_GETUSER = getString(R.string.URL_GETUSER);
        URL_UPDATEUSERPERMISSION = getString(R.string.URL_UPDATEUSERPERMISSION);
        loading = findViewById(R.id.loading_account);
        email = findViewById(R.id.text_email);
        btn_search = findViewById(R.id.btn_search);

        radio_group_role = findViewById(R.id.radio_group_role);
        text_view_user = findViewById(R.id.text_view_user);
        btn_permission = findViewById(R.id.btn_permission);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()) {
                    String email_input = email.getText().toString().trim();
                    search(email_input);
                }
            }
        });

        btn_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null) {
                    int radioId = radio_group_role.getCheckedRadioButtonId();
                    radioButton = findViewById(radioId);
                    String permission = radioButton.getText().toString();

                    changePermission(mUser.Id,permission);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_there_is_no_user), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // This is a method to show message with information about what radio button is selected.
    public void checkButton(View v) {
        int radioId = radio_group_role.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        Toast.makeText(this, getString(R.string.toast_selected_radio_button) + radioButton.getText(),
                Toast.LENGTH_SHORT).show();
    }

    // This method was created to find users by email address, we are searching user for change their permission level.
    // Method communicates with an external api which connects to the database and provides relevant information.
    // Method require email parameter.
    private void search(final String email) {
        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETUSER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("user");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    mUser = new User();
                                    mUser.Id = object.getString("id").trim();
                                    mUser.Name = object.getString("name").trim();
                                    mUser.LastName = object.getString("last_name").trim();
                                    mUser.Email = object.getString("email").trim();
                                    mUser.Password = object.getString("password").trim();
                                    mUser.Permission_level = object.getString("permission_level").trim();

                                    text_view_user.setText(getString(R.string.set_text_user)
                                            +getString(R.string.set_text_first_name) + mUser.Name
                                            +getString(R.string.set_text_last_name) + mUser.LastName
                                            +getString(R.string.set_text_email) + mUser.Email
                                    );
                                    loading.setVisibility(View.GONE);
                                }
                                if(mUser != null){
                                    int a=0, b=1, c=2;  // a -> admin, b-> bodyguard, c -> customer
                                    if(mUser.Permission_level.equals(getString(R.string.role_admin)) ){
                                        radio_group_role.check(radio_group_role.getChildAt(a).getId());
                                    } else if(mUser.Permission_level.equals(getString(R.string.role_bodyguard))){
                                        radio_group_role.check(radio_group_role.getChildAt(b).getId());
                                    } else if(mUser.Permission_level.equals(getString(R.string.role_customer))){
                                        radio_group_role.check(radio_group_role.getChildAt(c).getId());
                                    } else {
                                        radio_group_role.clearCheck();
                                    }
                                }
                            }
                            else {
                                text_view_user.setText(getString(R.string.toast_user_not_find));
                                loading.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_search_error) + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_search_error) + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Method is used for check that structure of provided email address is correct.
    private boolean validateEmail() {
        String email_input = email.getText().toString().trim();
        if (email_input.isEmpty()) {
            email.setError(getString(R.string.set_error_email_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_input).matches()) {
            email.setError(getString(R.string.set_error_email_validation));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    // This method was created to change user permission level.
    // Method communicates with an external api which connects to the database and update the searched user.
    // Method require id and permission parameter.
    private void changePermission(final String id, final String permission) {
        loading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATEUSERPERMISSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(getApplicationContext(),getString(R.string.toast_update_permission_success),Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),getString(R.string.toast_update_permission_faild), Toast.LENGTH_LONG).show();
                                loading.setVisibility(View.GONE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),getString(R.string.toast_update_permission_error) + e.toString(),Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.setVisibility(View.GONE);
                        btn_search.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),getString(R.string.toast_update_permission_error) + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("permission_level", permission);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
