package com.example.ticket_machine.ui.tickets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QrTicketViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public QrTicketViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is tickets fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
