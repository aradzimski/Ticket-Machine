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
    private Event mItem;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView resultText;
    BarcodeDetector barcodeDetector;

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

        surfaceView = (SurfaceView) rootView.findViewById(R.id.camera_preview);
        resultText = (TextView) rootView.findViewById(R.id.scan_result);

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080).build();

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
                    resultText.post(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

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

        return rootView;
    }
}
