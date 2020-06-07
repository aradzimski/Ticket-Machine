package com.example.ticket_machine.ui.tickets;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ticket_machine.R;
import com.example.ticket_machine.models.Ticket;
import com.example.ticket_machine.tools.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The following class is used to get ticket key from the database and show to as a QR Code.
 * This fragment retrieves ticket ID from TicketActivity.
 */

public class TicketFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private static String URL_GETTICKET;
    private Ticket mItem;
    private ImageView qrImage;
    private JsonParser jsonParser;

    public TicketFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.ticket_detail, container, false);

        jsonParser = new JsonParser();

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            URL_GETTICKET = getString(R.string.URL_GETTICKET);

            /**
             * Retrieve data about ticket and related event.
             */

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETTICKET,
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

                                        mItem = JsonParser.getTicket(object);

                                        if (mItem != null) {
                                            /**
                                             * If ticket is valid, set a bitmap with QR Code.
                                             */
                                            ((ImageView) rootView.findViewById(R.id.ticket_detail)).setImageBitmap(GenerateQRCode(mItem.Key));
                                        }
                                    }
                                }
                            } catch (JSONException | WriterException e){
                                e.printStackTrace();
                                Toast.makeText(getContext(),"Cannot get ticket. Error: " + e.toString(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(),"Cannot get ticket. Error: " + error.toString(),Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("ticket_id", getArguments().getString(ARG_ITEM_ID));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }

        return rootView;
    }

    /**
     * This method is used to generate a bitmap with QR Code, based on a ticket key.
     */
    public Bitmap GenerateQRCode(String key) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

            int dimensions = 800;

            /**
            * Encode ticket key as QR Code to the matrix.
            */
            BitMatrix bitMatrix = qrCodeWriter.encode(key, BarcodeFormat.QR_CODE, dimensions, dimensions);
            Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.RGB_565);

            /**
            * Iterate through the matrix to draw the bitmap, basing on values in each cell.
            */
            for (int x = 0; x < dimensions; x++)
            {
                for (int y = 0; y < dimensions; y++)
                {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
    }
}
