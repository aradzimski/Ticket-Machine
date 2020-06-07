package com.example.ticket_machine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.models.Ticket;
import com.example.ticket_machine.tools.SharedPreferenceConfig;
import com.example.ticket_machine.ui.tickets.TicketActivity;
import com.example.ticket_machine.ui.tickets.TicketFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsActivity extends AppCompatActivity {

    /**
     * The following class is based on Master-Detail Flow scheme.
     * It is used to generate ticket list with listeners, which will pass clicked ticket ID to the details view.
     */
    private boolean mTwoPane;
    private SharedPreferenceConfig preferenceConfig;
    private static String URL_GETUSERTICKETS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.ticket_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {

        preferenceConfig = new SharedPreferenceConfig(TicketsActivity.this);
        final String user_id = preferenceConfig.LoadUserId();

        URL_GETUSERTICKETS = getString(R.string.URL_GETUSERTICKETS);

        final List<Ticket> ticket_list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETUSERTICKETS,
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

                                    Ticket ticket = new Ticket();
                                    ticket.Id = object.getString("id").trim();
                                    ticket.EventId = object.getString("event_id").trim();
                                    ticket.UserId = object.getString("user_id").trim();
                                    ticket.Key = object.getString("key").trim();
                                    ticket.CreatedOn = object.getString("createdOn").trim();
                                    ticket.EventName = object.getString("name").trim();

                                    ticket_list.add(ticket);
                                }
                                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(TicketsActivity.this, ticket_list, mTwoPane));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TicketsActivity.this, "Cannot get tickets. Error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TicketsActivity.this, "Cannot get tickets. Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(TicketsActivity.this);
        requestQueue.add(stringRequest);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final TicketsActivity mParentActivity;
        private final List<Ticket> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ticket ticket = (Ticket) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(TicketFragment.ARG_ITEM_ID, ticket.Id);
                    TicketFragment fragment = new TicketFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, TicketActivity.class);
                    intent.putExtra(TicketFragment.ARG_ITEM_ID, ticket.Id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(TicketsActivity parent,
                                      List<Ticket> ticket_list,
                                      boolean twoPane) {
            mValues = ticket_list;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ticket_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).Id);
            holder.mContentView.setText(mValues.get(position).EventName);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}