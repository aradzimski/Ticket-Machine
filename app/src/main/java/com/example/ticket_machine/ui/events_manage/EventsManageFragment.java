package com.example.ticket_machine.ui.events_manage;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.Event;
import com.example.ticket_machine.models.User;
import com.example.ticket_machine.tools.EventsAdapter;
import com.example.ticket_machine.tools.UserAdapter;
import com.example.ticket_machine.ui.accounts.AccountsActivity;
import com.example.ticket_machine.ui.accounts.AccountsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The following class is used to show and delete events for users who have the admin privilege group.
 * The class also contains a button that allows you to go to another activity in which you can add another event.
 * EventsManageFragment class extends the fragment class.
 */
public class EventsManageFragment extends Fragment {
    private static String URL_EVENTS;
    private Button btn_add_event;

    private ListView events_list_view;
    private EventsAdapter eventsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_manage, container, false);

        URL_EVENTS = getString(R.string.URL_EVENTS);
        events_list_view =  view.findViewById(R.id.events_list_view);
        btn_add_event = view.findViewById(R.id.btn_add_event);

        /**
         * Create and fill the lists of users with admin and bodyguard permission level
         */
        getEvents(new VolleyCallbackEvent(){
            @Override
            public void onSuccess(ArrayList<Event> eventsList) {
                eventsAdapter = new EventsAdapter(getContext(), eventsList);
                events_list_view.setAdapter(eventsAdapter);
            }
        });

        /**
         * Below method on button allows you to go to another activity in which you can change the user's privileges.
         */
        btn_add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), EventsManageActivity.class);
                startActivity(in);
            }
        });
        return view;
    }

    public interface VolleyCallbackEvent{
        void onSuccess(ArrayList<Event> eventsList);
    }

    /**
     * This method was created to find users with admin and bodyguard role, we are searching user for show which user have which permission level.
     * Method communicates with an external api which connects to the database and provides relevant information.
     * Method use VolleyCallback interface parameter, which allow as to get data in onCreateView if the response was correct.
     * @param callback
     */
    private void getEvents(final EventsManageFragment.VolleyCallbackEvent callback) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString(getString(R.string.success));
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")) {
                                ArrayList<Event> eventsList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id").trim();
                                    String name = object.getString("name").trim();
                                    String description = object.getString("description").trim();
                                    String price = object.getString("price").trim();
                                    String date_of_start = object.getString("dateofstart").trim();
                                    String date_of_end = object.getString("dateofend").trim();

                                    eventsList.add(new Event(id, name, description, price, date_of_start, date_of_end));
                                }
                                callback.onSuccess(eventsList);
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
