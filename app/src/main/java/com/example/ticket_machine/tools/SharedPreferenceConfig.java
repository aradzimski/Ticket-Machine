package com.example.ticket_machine.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.ticket_machine.R;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreference;
    private Context context;

    public SharedPreferenceConfig(Context context){
        this.context = context;
        sharedPreference = context.getSharedPreferences(context.getResources().getString(R.string.login_preference),Context.MODE_PRIVATE);
    }

    public void SaveLoginStatus(boolean status){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean(context.getResources().getString(R.string.login_status_preference),status);
        editor.apply();
    }

    public boolean LoadLoginStatus(){
        boolean status = false;
        status  = sharedPreference.getBoolean(context.getResources().getString(R.string.login_status_preference),false);
        return status;
    }

    public void SaveUserRole(String role){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(context.getResources().getString(R.string.login_role_preference),role);
        editor.apply();
    }

    public String LoadUserRole(){
        String role = "0";
        role  = sharedPreference.getString(context.getResources().getString(R.string.login_role_preference) ,role);
        return role;
    }

    public void SaveUserId(String id){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(context.getResources().getString(R.string.login_id_preference), id);
        editor.apply();
    }

    public String LoadUserId(){
        String id = "0";
        id = sharedPreference.getString(context.getResources().getString(R.string.login_id_preference), id);
        return id;
    }
}