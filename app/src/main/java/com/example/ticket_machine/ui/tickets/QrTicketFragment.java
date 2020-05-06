package com.example.ticket_machine.ui.tickets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.SharedPreferenceConfig;

public class QrTicketFragment extends Fragment {

    private SharedPreferenceConfig preferenceConfig;
    private QrTicketViewModel qrTicketViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        qrTicketViewModel =
                ViewModelProviders.of(this).get(QrTicketViewModel.class);
        View view = inflater.inflate(R.layout.fragment_qrticket, container, false);
        final TextView textView = view.findViewById(R.id.text_tickets);
        qrTicketViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        preferenceConfig = new SharedPreferenceConfig(getContext());

        String ticket_id = this.getArguments().getString("ticket_id");

        return view;
    }
}
