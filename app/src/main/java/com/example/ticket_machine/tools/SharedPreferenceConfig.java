package com.example.ticket_machine.tools;
import android.content.Context;
import android.content.SharedPreferences;
import com.example.ticket_machine.R;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreference;
    private Context context;
    private String role_none;

    public SharedPreferenceConfig(Context context){
        this.context = context;
        role_none = context.getResources().getString(R.string.role_none);
        sharedPreference = context.getSharedPreferences(context.getResources().getString(R.string.login_preference),Context.MODE_PRIVATE);
    }

    public void SaveUserRole(String role){
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(context.getResources().getString(R.string.login_role_preference),role);
        editor.apply();
    }

    public String LoadUserRole(){
        String role = role_none;
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