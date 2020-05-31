package com.example.ticket_machine.ui.scanner;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.SharedPreferenceConfig;

public class ScannerFragment extends Fragment {
    private SharedPreferenceConfig preferenceConfig;
    private ScannerViewModel scannerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel =
                ViewModelProviders.of(this).get(ScannerViewModel.class);
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        final TextView textView = view.findViewById(R.id.text_events);

        preferenceConfig = new SharedPreferenceConfig(getContext());
        return view;
    }
}
