package com.example.ticket_machine.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is register fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
