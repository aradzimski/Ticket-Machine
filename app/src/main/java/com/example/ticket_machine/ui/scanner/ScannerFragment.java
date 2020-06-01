package com.example.ticket_machine.ui.scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.ticket_machine.R;

public class ScannerFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

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

//        if (getArguments().containsKey(ARG_ITEM_ID)) {
//    }

        return rootView;
    }
}
