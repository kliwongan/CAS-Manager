package com.casmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.casmanager.SharedPref;
import com.casmanager.fragment.ActivityFragment;
import com.casmanager.fragment.LoggerFragment;
import com.casmanager.fragment.ReflectionFragment;
import com.google.android.material.navigation.NavigationView;
import com.casmanager.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Initializing all the view objects used in the homepage
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    MenuItem darkTheme;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightMode() == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //Initializing toolbar in main activity
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting up navigation drawer views and selected item listener to determine which item is being selected
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        darkTheme = findViewById(R.id.darkTheme);

        //Sets up action bar menu collapsible toggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        switch(sharedPref.getFragmentState()) {
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                sharedPref.setFragmentState(1);
                fragmentTransaction.replace(R.id.container_fragment, new ActivityFragment(), "ACTIVITIES");
                navigationView.setCheckedItem(R.id.activities);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                sharedPref.setFragmentState(2);
                navigationView.setCheckedItem(R.id.reflections);
                fragmentTransaction.replace(R.id.container_fragment, new ReflectionFragment(), "REFLECTIONS");
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                sharedPref.setFragmentState(3);
                navigationView.setCheckedItem(R.id.logger);
                fragmentTransaction.replace(R.id.container_fragment, new LoggerFragment(), "LOGGER");
                fragmentTransaction.commit();
                break;
        }

    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.activities) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            sharedPref.setFragmentState(1);
            fragmentTransaction.replace(R.id.container_fragment, new ActivityFragment(), "ACTIVITIES");
            fragmentTransaction.commit();
        }

        if (menuItem.getItemId() == R.id.reflections) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            sharedPref.setFragmentState(2);
            fragmentTransaction.replace(R.id.container_fragment, new ReflectionFragment(), "REFLECTIONS");
            fragmentTransaction.commit();
        }

        if (menuItem.getItemId() == R.id.logger) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            sharedPref.setFragmentState(3);
            fragmentTransaction.replace(R.id.container_fragment, new LoggerFragment(), "LOGGER");
            fragmentTransaction.commit();
        }

        if (menuItem.getItemId()==R.id.darkTheme) {
            if (sharedPref.loadNightMode() == true) {
                sharedPref.setNightModeState(false);
                restartApp();
            } else {
                sharedPref.setNightModeState(true);
                restartApp();
            }
        }

        if(menuItem.getItemId() == R.id.export) {
            Intent i = new Intent(getApplicationContext(), ExportActivity.class);
            startActivity(i);
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}
