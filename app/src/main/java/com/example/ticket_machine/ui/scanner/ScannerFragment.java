package com.example.ticket_machine.ui.scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScannerFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private static String URL_GETEVENT;
    private Event mItem;

    public ScannerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.scanner_detail, container, false);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            URL_GETEVENT = getString(R.string.URL_GETEVENT);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETEVENT,
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

                                        mItem = new Event();
                                        mItem.Id = object.getString("id").trim();
                                        mItem.Name = object.getString("name").trim();
                                        mItem.Description = object.getString("description").trim();
                                        mItem.Price = object.getString("price").trim();

                                        if (mItem != null) {
                                            ((TextView) rootView.findViewById(R.id.scanner_detail)).setText(mItem.Name);
                                        }
                                    }
                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getContext(),"Cannot get event. Error: " + e.toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(),"Cannot get event. Error: " + error.toString(),Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("id", getArguments().getString(ARG_ITEM_ID));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }

        return rootView;
    }
}
