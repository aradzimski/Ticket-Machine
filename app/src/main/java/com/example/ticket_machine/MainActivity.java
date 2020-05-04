package com.example.ticket_machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.ticket_machine.tools.SharedPreferenceConfig;
import com.example.ticket_machine.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferenceConfig preferenceConfig;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //showedMenuItem();

        if(preferenceConfig.LoadLoginStatus() == false){
            hideItemBeforeLogin();
            //Toast.makeText(getApplicationContext(), "Preference on create is: "+preferenceConfig.LoadLoginStatus(), Toast.LENGTH_LONG).show();
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_register,
                R.id.nav_login,
                R.id.nav_events,
                R.id.nav_tickets)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //showedMenuItem();

        if(preferenceConfig.LoadLoginStatus() == true & preferenceConfig.LoadUserRole().equals("1")){
            showItemAfterLogin();
            //Toast.makeText(getApplicationContext(),"Preference: "+preferenceConfig.LoadLoginStatus() + " Role: " + preferenceConfig.LoadUserRole(), Toast.LENGTH_LONG).show();
        }
        if(preferenceConfig.LoadLoginStatus() == false) {
            hideItemBeforeLogin();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout :
                preferenceConfig.SaveLoginStatus(false);
                moveToNewActivity();
                Toast.makeText(this,getResources().getString(R.string.success_logout),Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings :
                Toast.makeText(this,getResources().getString(R.string.action_settings),Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // To hide element from navigation menu !!!!
    private void hideItemBeforeLogin() {
        Menu nav_Menu = navigationView.getMenu();

        //nav_Menu.findItem(R.id.nav_register).setVisible(true);
        //nav_Menu.findItem(R.id.nav_login).setVisible(true);

        nav_Menu.findItem(R.id.nav_tickets).setVisible(false);
        nav_Menu.findItem(R.id.nav_events).setVisible(false);
    }

    // To show element from navigation menu !!!!
    private void showItemAfterLogin(){
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_register).setVisible(false);
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
        nav_Menu.findItem(R.id.nav_tickets).setVisible(true);
        nav_Menu.findItem(R.id.nav_events).setVisible(true);
    }

/*
    public void showedMenuItem(){
        String permission_level = preferenceConfig.LoadUserRole();

        switch(permission_level){
            case "1":
                showItemAfterLogin();
                Toast.makeText(getApplicationContext(),"Preference: "+preferenceConfig.LoadLoginStatus() + " Role: " + preferenceConfig.LoadUserRole(), Toast.LENGTH_LONG).show();
            case "2":
                showItemAfterLogin();
            case "3":
                showItemAfterLogin();
            default:
                hideItemBeforeLogin();
        }

    }
*/
    private void moveToNewActivity () {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
        (this).overridePendingTransition(0, 0); // (0,0) it means no animation on transition

    }
}