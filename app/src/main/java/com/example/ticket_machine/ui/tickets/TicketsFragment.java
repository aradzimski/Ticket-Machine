package com.example.ticket_machine.ui.tickets;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TicketsFragment extends Fragment {

    private SharedPreferenceConfig preferenceConfig;
    private TicketsViewModel ticketsViewModel;
    private static String URL_GETUSERTICKETS;
//    private EditText editText;
//    private ImageView imageView;
//    private Button generateQRButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ticketsViewModel =
                ViewModelProviders.of(this).get(TicketsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        final TextView textView = view.findViewById(R.id.text_tickets);
        ticketsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        preferenceConfig = new SharedPreferenceConfig(getContext());
        final String user_id = preferenceConfig.LoadUserId();

        final TableLayout ticketTable = view.findViewById(R.id.ticket_table);

        URL_GETUSERTICKETS = getString(R.string.URL_GETUSERTICKETS);

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

                                    String id = object.getString("id").trim();
                                    String event_id = object.getString("event_id").trim();
                                    String user_id = object.getString("user_id").trim();
                                    String key = object.getString("key").trim();
                                    String createdOn = object.getString("createdOn").trim();
                                    String event_name = object.getString("name").trim();

                                    /// Printing all events to table
                                    TableRow ticketRow = new TableRow(getContext());
                                    TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                                            TableLayout.LayoutParams.WRAP_CONTENT,
                                            TableLayout.LayoutParams.WRAP_CONTENT);
                                    rowParams.setMargins(0, 10, 0, 10);
                                    ticketRow.setLayoutParams(rowParams);
                                    ticketRow.setId(Integer.parseInt(id));
                                    ticketRow.setBackgroundColor(Color.GRAY);
                                    ticketRow.setWeightSum(2);

                                    TextView eventTitle = new TextView(getContext());
                                    eventTitle.setId(Integer.parseInt(id));
                                    eventTitle.setText(event_name);
                                    eventTitle.setTextColor(Color.WHITE);
                                    eventTitle.setTextSize(14);
                                    eventTitle.setGravity(Gravity.FILL_VERTICAL);
                                    eventTitle.setPadding(10,10,10,10);

                                    ticketRow.addView(eventTitle);

                                    TextView ticketCreationDate = new TextView(getContext());
                                    ticketCreationDate.setId(Integer.parseInt(id));
                                    ticketCreationDate.setText(createdOn);
                                    ticketCreationDate.setTextColor(Color.WHITE);
                                    ticketCreationDate.setTextSize(12);
                                    ticketCreationDate.setGravity(Gravity.FILL_VERTICAL);
                                    ticketCreationDate.setPadding(10,10,10,10);

                                    ticketRow.addView(ticketCreationDate);

                                    ticketTable.addView(ticketRow);
                                    ticketTable.setShrinkAllColumns(true);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Cannot get tickets. Error: " + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Cannot get tickets. Error: " + error.toString(),Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", user_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

//        editText = view.findViewById(R.id.editText);
//        imageView = view.findViewById(R.id.imageView);
//        generateQRButton = view.findViewById(R.id.generateQRButton);
//
//        generateQRButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View  v){
//                GenerateQRCode();
//            }
//        });

        return view;
    }

//    public void GenerateQRCode() {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        try {
//            BitMatrix bitMatrix = qrCodeWriter.encode(editText.getText().toString(), BarcodeFormat.QR_CODE, 284, 284);
//            Bitmap bitmap = Bitmap.createBitmap(284, 284, Bitmap.Config.RGB_565);
//
//            for (int x = 0; x < 284; x++)
//            {
//                for (int y = 0; y < 284; y++)
//                {
//                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
//                }
//            }
//
//            imageView.setImageBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
