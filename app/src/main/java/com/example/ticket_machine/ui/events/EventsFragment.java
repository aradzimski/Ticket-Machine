package com.example.ticket_machine.ui.events;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.SharedPreferenceConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The following class is used to generate view with table that contains all active events with possibility of buying ticket for chosen event.
 * EventsFragment class extends the fragment class.
 */

public class EventsFragment extends Fragment {

    private SharedPreferenceConfig preferenceConfig;
    private EventsViewModel eventsViewModel;
    private TableLayout eventTable;
    private static String URL_EVENTS;
    private static String URL_ADDTICKET;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventsViewModel =
                ViewModelProviders.of(this).get(EventsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        final TextView textView = view.findViewById(R.id.text_events);
        eventsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        preferenceConfig = new SharedPreferenceConfig(getContext());

        final TableLayout eventTable = view.findViewById(R.id.event_table);

        URL_EVENTS = getString(R.string.URL_EVENTS);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();
                                    String name = object.getString("name").trim();
                                    String description = object.getString("description").trim();
                                    String price = object.getString("price").trim();

                                    /**
                                     * Prepare table row for each event returned from API.
                                     */

                                    TableRow eventRow = new TableRow(getContext());
                                    TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                                            TableLayout.LayoutParams.WRAP_CONTENT,
                                            TableLayout.LayoutParams.WRAP_CONTENT);
                                    rowParams.setMargins(0, 10, 0, 10);
                                    eventRow.setLayoutParams(rowParams);
                                    eventRow.setId(Integer.parseInt(id));
                                    eventRow.setBackgroundColor(Color.GRAY);
                                    eventRow.setWeightSum(2);

                                    TextView eventTitle = new TextView(getContext());
                                    eventTitle.setId(Integer.parseInt(id));
                                    eventTitle.setText(name);
                                    eventTitle.setTextColor(Color.WHITE);
                                    eventTitle.setTextSize(14);
                                    eventTitle.setGravity(Gravity.FILL_VERTICAL);
                                    eventTitle.setPadding(10,10,300,10);

                                    eventRow.addView(eventTitle);

                                    TextView eventPrice = new TextView(getContext());
                                    eventPrice.setId(Integer.parseInt(id));
                                    eventPrice.setText(price);
                                    eventPrice.setTextColor(Color.RED);
                                    eventPrice.setTextSize(15);
                                    eventPrice.setGravity(Gravity.FILL_VERTICAL);
                                    eventPrice.setPadding(10,10,10,10);

                                    eventRow.addView(eventPrice);

                                    Button eventButton = new Button(getContext());
                                    eventButton.setId(Integer.parseInt(id));
                                    eventButton.setText(R.string.btn_buyticket);
                                    eventButton.setTextSize(12);
                                    eventButton.setPadding(10,10,10,10);
                                    eventButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            buyTicket(v);
                                        }
                                    });

                                    eventRow.addView(eventButton);

                                    eventTable.addView(eventRow);
                                    eventTable.setShrinkAllColumns(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Events List Error" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Events List Error" + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

        return view;
    }

    /**
     * This method is called on button click. Every row in the events table has a button with unique ID number, which determines the event chosen by the user.
     * Then we get ID of the logged in user and generate unique key for a new ticket.
     * With event ID, user ID and ticket KEY the request is sent to API to create a new ticket entry in database.
     */

    public void buyTicket(View v)
    {
        final String event_id = Integer.toString(v.getId());
        final String user_id = preferenceConfig.LoadUserId();
        final String key = UUID.randomUUID().toString().replaceAll("-", "");

        URL_ADDTICKET = getString(R.string.URL_ADDTICKET);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDTICKET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(getContext(),"Ticket ID: " + key,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Cannot buy ticket. Error: " + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Cannot buy ticket. Error: " + error.toString(),Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("event_id", event_id);
                params.put("user_id", user_id);
                params.put("key", key);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}