package com.example.ticket_machine.ui.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScannerFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private static String URL_GETEVENT;
    private static String URL_GETTICKETBYKEY;
    private static String URL_UPDATETICKETSTATUS;
    private String lastScannedKey;
    private Event mItem;
    private SurfaceView surfaceView;
    private CameraSource cameraSource;
    private TextView resultText;
    private BarcodeDetector barcodeDetector;

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

        URL_GETTICKETBYKEY = getString(R.string.URL_GETTICKETBYKEY);
        URL_GETEVENT = getString(R.string.URL_GETEVENT);
        URL_UPDATETICKETSTATUS = getString(R.string.URL_UPDATETICKETSTATUS);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.camera_preview);
        resultText = (TextView) rootView.findViewById(R.id.scan_result);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    50);
        }

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080).build();

        if (getArguments().containsKey(ARG_ITEM_ID)) {

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
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Cannot get event. Error: " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Cannot get event. Error: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", getArguments().getString(ARG_ITEM_ID));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }

        lastScannedKey = "";

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    if (!lastScannedKey.equals(qrCodes.valueAt(0).rawValue)) {

                        resultText.post(new Runnable() {
                            @Override
                            public void run() {

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_GETTICKETBYKEY,
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

                                                            String ticket_status = object.getString("active").trim();
                                                            String ticket_eventid = object.getString("event_id").trim();

                                                            if (ticket_status.equals("1") && ticket_eventid.equals(getArguments().getString(ARG_ITEM_ID))) {
                                                                resultText.setTextColor(ContextCompat.getColor(getContext(), R.color.ticket_valid));
                                                                resultText.setText(getString(R.string.ticket_valid));

                                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATETICKETSTATUS,
                                                                        new Response.Listener<String>() {
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                try {
                                                                                    JSONObject jsonObject = new JSONObject(response);
                                                                                    String success = jsonObject.getString("success");
                                                                                    if (success.equals("1")) {
                                                                                        Toast.makeText(getContext(), "Ticket status has been updated.", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                    Toast.makeText(getContext(), "Cannot get ticket. Error: " + e.toString(), Toast.LENGTH_LONG).show();
                                                                                }
                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(getContext(), "Cannot get ticket. Error: " + error.toString(), Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }) {
                                                                    @Override
                                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                                        Map<String, String> params = new HashMap<>();
                                                                        params.put("key", qrCodes.valueAt(0).rawValue);
                                                                        return params;
                                                                    }
                                                                };

                                                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                                                requestQueue.add(stringRequest);

                                                            } else {
                                                                resultText.setTextColor(ContextCompat.getColor(getContext(), R.color.ticket_invalid));
                                                                resultText.setText(getString(R.string.ticket_invalid));
                                                            }
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getContext(), "Cannot get ticket. Error: " + e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getContext(), "Cannot get ticket. Error: " + error.toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("key", qrCodes.valueAt(0).rawValue);
                                        return params;
                                    }
                                };

                                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                requestQueue.add(stringRequest);
                                lastScannedKey = qrCodes.valueAt(0).rawValue;
                            }
                        });
                    }
                }
            }
        });

        return rootView;
    }
}