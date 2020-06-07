package com.example.ticket_machine.ui.events_manage;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.MainActivity;
import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.PatternsForValidation;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The following class is used to add events, it is only accessible for users who have the admin privilege group.
 * Adding a new event is based on completing the form and saving. The form is validated before being saved.
 * EventsManageActivity class extends the AppCompatActivity class.
 */
public class EventsManageActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static String URL_ADDEVENT;
    private EditText event_name,event_description,event_price,event_date_of_start,event_date_of_end;
    private Button btn_start, btn_end, btn_save;
    private int y, m, d, h, min, s;
    private int dateFieldFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_manage);

        URL_ADDEVENT = getString(R.string.URL_ADDEVENT);

        event_date_of_start = findViewById(R.id.event_date_of_start);
        event_date_of_end = findViewById(R.id.event_date_of_end);
        event_name = findViewById(R.id.event_name);
        event_description = findViewById(R.id.event_description);
        event_price = findViewById(R.id.event_price);

        btn_start = findViewById(R.id.btn_date_time_of_start_event);
        btn_end = findViewById(R.id.btn_date_time_of_end_event);
        btn_save = findViewById(R.id.btn_save_event);

        /**
         * Button listener to pick up date time for event start
         */
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFieldFlag = 0;
                showDatePickerDialog();
            }
        });

        /**
         * Button listener to pick up date time for event end
         */
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFieldFlag = 1;
                showDatePickerDialog();
            }
        });

        /**
         * Button listener to save event
         */
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInputAndSaveEvent(v);
            }
        });
    }

    /**
     * A method created to retrieve a date based on a calendar and to call the clock view to retrieve time.
     */
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    /**
     * A method created to retrieve time based on a clock view.
     */
    public void showTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    /**
     * Required method for implementing the TimePickerDialog class, below parameters do not require translation.
     * Additionally, the method introduces the mechanism of cleaning auxiliary fields and save year, month and day in temporary fields.
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        y = m = d = h = min = s = 0;
        y = year;
        m = month;
        d = dayOfMonth;

        showTimePickerDialog();
    }

    /**
     * Required method for implementing the DatePickerDialog class, accepted parameters do not require translation.
     * Method saves hour and minute in temporary fields, then we create pattern of date time format ,
     * next we save formatted date time to specific field on view.
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        h = hourOfDay;
        min = minute;

        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String formattedDate = simpleDateFormat.format(new Date(y - 1900, m, d, h, min, s));

        if(dateFieldFlag == 0){
            event_date_of_start.setText(formattedDate);
        }
        if(dateFieldFlag == 1){
            event_date_of_end.setText(formattedDate);
        }
    }

    /**
     * Method is used for check that structure of provided event name is correct.
     * @return
     */
    private boolean validateName() {
        String name_input = event_name.getText().toString().trim();
        if (name_input.isEmpty()) {
            event_name.setError(getString(R.string.error_field_empty));
            return false;
        } else if (!PatternsForValidation.TITLE_PATTERN.matcher(name_input).matches()) {
            event_name.setError(getString(R.string.error_not_valid_2_25));
            return false;
        } else {
            event_name.setError(null);
            return true;
        }
    }

    /**
     * Method is used for check that structure of provided description is correct.
     * @return
     */
    private boolean validateDescription() {
        String event_description_input = event_description.getText().toString().trim();
        if (event_description_input.isEmpty()) {
            event_description.setError(getString(R.string.error_field_empty));
            return false;
        } else if (!PatternsForValidation.DESCRIPTION_PATTERN.matcher(event_description_input).matches()) {
            event_description.setError(getString(R.string.error_not_valid_5_50));
            return false;
        } else {
            event_description.setError(null);
            return true;
        }
    }

    /**
     * Method is used for check that structure of provided event price is correct.
     * @return
     */
    private boolean validatePrice() {
        String event_price_input = event_price.getText().toString().trim();
        if (event_price_input.isEmpty()) {
            event_price.setError(getString(R.string.error_field_empty));
            return false;
        } else {
            event_price.setError(null);
            return true;
        }
    }

    /**
     * Method is used for check that structure of provided event start date is correct.
     * @return
     */
    private boolean validateStartDate() {
        String event_date_of_start_input = event_date_of_start.getText().toString().trim();
        if (event_date_of_start_input.isEmpty()) {
            event_date_of_start.setError(getString(R.string.error_field_empty));
            return false;
        } else {
            event_date_of_start.setError(null);
            return true;
        }
    }

    /**
     * Method is used for check that structure of provided event end date is correct.
     * @return
     */
    private boolean validateEndDate() {
        String event_date_of_end_input = event_date_of_end.getText().toString().trim();
        if (event_date_of_end_input.isEmpty()) {
            event_date_of_end.setError(getString(R.string.error_field_empty));
            return false;
        } else {
            event_date_of_start.setError(null);
            return true;
        }
    }

    /**
     * Method is used for check that structure of provided event fields are all correct. If all of them are correct,
     * procedure saveEvent will by launched. Otherwise, error messages will be displayed next to the input fields.
     * @return
     */
    private void confirmInputAndSaveEvent(View v) {
        if (!validateName() | !validateDescription() | !validatePrice() | !validateStartDate() | !validateEndDate()) {
            return;
        }else{
            String ev_name = event_name.getText().toString().trim();
            String ev_description = event_description.getText().toString().trim();
            String ev_price = event_price.getText().toString().trim();
            String ev_date_of_start = event_date_of_start.getText().toString().trim();
            String ev_date_of_end = event_date_of_end.getText().toString().trim();

            saveEvent(ev_name, ev_description, ev_price, ev_date_of_start, ev_date_of_end);
        }
    }

    /**
     * This method was created to add event.
     * Method communicates with an external api which connects to the database and add the event.
     * Method require below parameters, this parameter are simple and do not require translation.
     * @param ev_name
     * @param ev_description
     * @param ev_price
     * @param ev_start
     * @param ev_end
     */
    public void saveEvent(final String ev_name, final String ev_description, final String ev_price, final String ev_start, final String ev_end){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADDEVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(getApplicationContext(),getString(R.string.toast_add_event_success),Toast.LENGTH_LONG).show();
                                moveToNewActivity();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),getString(R.string.toast_add_event_failure), Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),getString(R.string.response_catch_error) + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),getString(R.string.response_listener_error) + error.toString(),Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("name", ev_name);
                params.put("description", ev_description);
                params.put("price", ev_price);
                params.put("dateofstart", ev_start);
                params.put("dateofend", ev_end);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    /**
     * The task of this method is to transfer us to another activity. In this case it is MainActivity.
     */
    private void moveToNewActivity () {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }


}
