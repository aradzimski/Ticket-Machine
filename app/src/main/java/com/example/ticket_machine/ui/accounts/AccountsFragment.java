package com.example.ticket_machine.ui.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ticket_machine.R;
import com.example.ticket_machine.tools.SharedPreferenceConfig;

public class AccountsFragment extends Fragment {
    private SharedPreferenceConfig preferenceConfig;
    private AccountsViewModel accountsViewModel;
    private Button btn_add;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountsViewModel =
                ViewModelProviders.of(this).get(AccountsViewModel.class);
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);
        final TextView textView = view.findViewById(R.id.text_events);
        accountsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        preferenceConfig = new SharedPreferenceConfig(getContext());

        btn_add = view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), AccountsActivity.class);
                startActivity(in);
            }
        });
        return view;
    }
}
