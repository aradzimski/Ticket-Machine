package com.example.ticket_machine.ui.scanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public ScannerViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is register fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
