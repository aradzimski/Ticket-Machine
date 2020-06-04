package com.example.ticket_machine.ui.accounts;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.User;
import com.example.ticket_machine.tools.UserAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

// The following class is used to show users who have the admin and bodyguard privileges group.
// The class also contains a button that allows you to go to another activity in which you can change the user's privileges.
// AccountsFragment class extends the fragment class.
public class AccountsFragment extends Fragment {
    private static String URL_GETPRIVILEGEDUSERS;
    private AccountsViewModel accountsViewModel;
    private Button btn_add;

    private ListView users_list_view_admin;
    private ListView users_list_view_bodyguard;
    private UserAdapter userAdminAdapter;
    private UserAdapter userBodyguardAdapter;

    public AccountsFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountsViewModel =
                ViewModelProviders.of(this).get(AccountsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        URL_GETPRIVILEGEDUSERS = getString(R.string.URL_GETPRIVILEGEDUSERS);
        users_list_view_admin =  view.findViewById(R.id.users_list_view_admin);
        users_list_view_bodyguard = view.findViewById(R.id.users_list_view_bodyguard);
        btn_add = view.findViewById(R.id.btn_add);

        // Create and fill the lists of users with admin and bodyguard permission level
        getPrivilegedUsers(new VolleyCallback(){
            @Override
            public void onSuccess(ArrayList<User> adminList, ArrayList<User> bodyguardList) {
                userAdminAdapter = new UserAdapter(getContext(), adminList);
                users_list_view_admin.setAdapter(userAdminAdapter);

                userBodyguardAdapter = new UserAdapter(getContext(), bodyguardList);
                users_list_view_bodyguard.setAdapter(userBodyguardAdapter);
            }
        });

        // Below method on button allows you to go to another activity in which you can change the user's privileges.
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), AccountsActivity.class);
                startActivity(in);
            }
        });

        return view;
    }

    // We call the onResume method because after returning to the fragment class we want to refresh the list of authorized users
    @Override
    public void onResume() {
        super.onResume();
        getPrivilegedUsers(new VolleyCallback(){
            @Override
            public void onSuccess(ArrayList<User> adminList, ArrayList<User> bodyguardList) {
                userAdminAdapter = new UserAdapter(getContext(), adminList);
                users_list_view_admin.setAdapter(userAdminAdapter);

                userBodyguardAdapter = new UserAdapter(getContext(), bodyguardList);
                users_list_view_bodyguard.setAdapter(userBodyguardAdapter);
            }
        });
    }

    public interface VolleyCallback{
        void onSuccess(ArrayList<User> adminList, ArrayList<User> bodyguardList);
    }

    // This method was created to find users with admin and bodyguard role, we are searching user for show which user have which permission level.
    // Method communicates with an external api which connects to the database and provides relevant information.
    // Method use VolleyCallback interface parameter, which allow as to get data in onCreateView if the response was correct.
    private void getPrivilegedUsers(final VolleyCallback callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETPRIVILEGEDUSERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString(getString(R.string.success));
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                ArrayList<User> usersAdminList = new ArrayList<>();
                                ArrayList<User> usersBodyguardList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String name = object.getString("name").trim();
                                    String last_name = object.getString("last_name").trim();
                                    String email = object.getString("email").trim();
                                    String permission_level = object.getString("permission_level").trim();

                                    if(permission_level.equals("admin")){
                                        usersAdminList.add(new User(name , last_name, email));
                                    }
                                    if(permission_level.equals("bodyguard")) {
                                        usersBodyguardList.add(new User(name , last_name, email));
                                    }
                                }
                                callback.onSuccess(usersAdminList, usersBodyguardList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.response_catch_error) + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.response_listener_error) + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
