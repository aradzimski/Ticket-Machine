package com.example.ticket_machine.ui.accounts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountsViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public AccountsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is register fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
