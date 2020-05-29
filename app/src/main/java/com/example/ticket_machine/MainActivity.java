package com.example.ticket_machine;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.ticket_machine.tools.SharedPreferenceConfig;
import com.example.ticket_machine.ui.home.HomeFragment;
import com.example.ticket_machine.ui.tickets.TicketActivity;
import com.example.ticket_machine.ui.tickets.TicketFragment;
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
    private String role_none,role_customer,role_bodyguard,role_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Get role name from resources.
        role_none = getResources().getString(R.string.role_none);
        role_customer = getResources().getString(R.string.role_customer);
        role_bodyguard = getResources().getString(R.string.role_bodyguard);
        role_admin = getResources().getString(R.string.role_admin);

        // Show proper item in navigation menu, depends on user permissions level.
        showedMenuItem();

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
        // Show proper item in navigation menu, depends on user permissions level.
        showedMenuItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout :
                String permission_level = preferenceConfig.LoadUserRole();
                if(permission_level.equals(role_none)){
                    Toast.makeText(this,getResources().getString(R.string.can_not_logout),Toast.LENGTH_SHORT).show();
                }else{
                    preferenceConfig.SaveUserRole(role_none);
                    moveToNewActivity();
                    Toast.makeText(this,getResources().getString(R.string.success_logout),Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_settings :
                Toast.makeText(this,getResources().getString(R.string.text_testtings),Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_tickets :
                Context context = getApplicationContext();
                Intent intent = new Intent(context, TicketsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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

    //  To show base element from navigation menu before login to app !
    private void itemBeforeLogin(Menu nav) {
        nav.findItem(R.id.nav_tickets).setVisible(false);
        nav.findItem(R.id.nav_events).setVisible(false);
        nav.findItem(R.id.nav_register).setVisible(true);
        nav.findItem(R.id.nav_login).setVisible(true);
    }

    // To hide base element from navigation menu after login to app !
    private void itemAfterLogin(Menu nav){
        nav.findItem(R.id.nav_register).setVisible(false);
        nav.findItem(R.id.nav_login).setVisible(false);
    }

    public void showedMenuItem(){
        String permission_level = preferenceConfig.LoadUserRole();
        Menu nav_Menu = navigationView.getMenu();

        if(permission_level.equals(role_customer)){
            itemAfterLogin(nav_Menu);
            nav_Menu.findItem(R.id.nav_tickets).setVisible(true);
            nav_Menu.findItem(R.id.nav_events).setVisible(true);
            Toast.makeText(getApplicationContext()," Role: " + preferenceConfig.LoadUserRole(), Toast.LENGTH_LONG).show();
        }else if(permission_level.equals(role_bodyguard)){
            itemAfterLogin(nav_Menu);
            nav_Menu.findItem(R.id.nav_tickets).setVisible(false);
            nav_Menu.findItem(R.id.nav_events).setVisible(true);
        }else if(permission_level.equals(role_admin)){
            itemAfterLogin(nav_Menu);
            nav_Menu.findItem(R.id.nav_tickets).setVisible(true);
            nav_Menu.findItem(R.id.nav_events).setVisible(false);
        }else{
            itemBeforeLogin(nav_Menu);
        }
        /* Switch looks better but you can't use it because "case" requires constant variables,
        *  we can not get them from resources.
        * *
        switch(permission_level){
            case "customer":
                itemAfterLogin(nav_Menu);
                nav_Menu.findItem(R.id.nav_tickets).setVisible(true);
                nav_Menu.findItem(R.id.nav_events).setVisible(true);
                Toast.makeText(getApplicationContext(),"Preference: "+preferenceConfig.LoadLoginStatus() + " Role: " + preferenceConfig.LoadUserRole(), Toast.LENGTH_LONG).show();
                break;
            case "bodyguard":
                itemAfterLogin(nav_Menu);
                nav_Menu.findItem(R.id.nav_tickets).setVisible(false);
                nav_Menu.findItem(R.id.nav_events).setVisible(true);
                break;
            case "admin":
                itemAfterLogin(nav_Menu);
                nav_Menu.findItem(R.id.nav_tickets).setVisible(true);
                nav_Menu.findItem(R.id.nav_events).setVisible(false);
                break;
            default:
                itemBeforeLogin(nav_Menu);
        }
        */
    }

    private void moveToNewActivity () {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
        (this).overridePendingTransition(0, 0); // (0,0) it means no animation on transition
    }
}