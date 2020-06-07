package com.example.ticket_machine.tools;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.MainActivity;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.Event;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ArrayAdapter class allows operation only on the string ArrayList,
 * so we extends the following class for use on list of event objects
 */
public class EventsAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> eventsList;
    private static String URL_DELETEEVENT;

    /**
     * Class constructor in which we give basic information when creating an object
     * @param context
     * @param list
     */
    public EventsAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Event> list) {
        super(context, 0 , list);
        mContext = context;
        eventsList = list;
        URL_DELETEEVENT = mContext.getString(R.string.URL_DELETEEVENT);
    }

    /**
     * Below method allow as to create an item of list view which will have Name, Description, Price, DateOfStart, DateOfEnd
     * Inside method we are getting TextView and Button elements from list_item resources and fill them with data from specific event
     * @param position
     * @param convertView
     * @param parent
     * @return list view of event
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.events_list_item,parent,false);

        final Event currentEvent = eventsList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.text_view_name_event);
        name.setText(currentEvent.Name);

        TextView description = (TextView) listItem.findViewById(R.id.text_view_description_event);
        description.setText(currentEvent.Description);

        TextView price = (TextView) listItem.findViewById(R.id.text_view_price_event);
        price.setText(mContext.getString(R.string.list_item_text_helper_price)+currentEvent.Price);

        TextView date_of_start = (TextView) listItem.findViewById(R.id.text_view_date_of_start_event);
        date_of_start.setText(mContext.getString(R.string.list_item_text_helper_date_of_start)+currentEvent.DateOfStart);

        TextView date_of_end = (TextView) listItem.findViewById(R.id.text_view_date_of_end_event);
        date_of_end.setText(mContext.getString(R.string.list_item_text_helper_date_of_end)+currentEvent.DateOfEnd);


        Button btn_delete = (Button) listItem.findViewById(R.id.btn_delete_event);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(currentEvent.Id);
            }
        });

        return listItem;
    }

    /**
     * The method enables deleting an event if a ticket has not been purchased for the event.
     * @param id this parameter is the event identifier
     */
    private void deleteEvent(final String id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETEEVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(getContext(), mContext.getString(R.string.toast_delete_success),Toast.LENGTH_LONG).show();
                                moveToNewActivity();
                            }
                            else {
                                Toast.makeText(getContext(), mContext.getString(R.string.toast_delete_faild), Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getContext(),mContext.getString(R.string.response_catch_error) + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),mContext.getString(R.string.response_listener_error) + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    /**
     * After deleting the event, we move the user to the main view.
     */
    private void moveToNewActivity () {
        Intent i = new Intent(mContext, MainActivity.class);
        mContext.startActivity(i);
    }
}
