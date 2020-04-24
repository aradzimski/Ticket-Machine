package com.example.ticket_machine.ui.tickets;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ticket_machine.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class TicketsFragment extends Fragment {

    private TicketsViewModel ticketsViewModel;
    private EditText editText;
    private ImageView imageView;
    private Button generateQRButton;

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

        editText = view.findViewById(R.id.editText);
        imageView = view.findViewById(R.id.imageView);
        generateQRButton = view.findViewById(R.id.generateQRButton);

        generateQRButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View  v){
                GenerateQRCode();
            }
        });

        return view;
    }

    public void GenerateQRCode() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(editText.getText().toString(), BarcodeFormat.QR_CODE, 284, 284);
            Bitmap bitmap = Bitmap.createBitmap(284, 284, Bitmap.Config.RGB_565);

            for (int x = 0; x < 284; x++)
            {
                for (int y = 0; y < 284; y++)
                {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
