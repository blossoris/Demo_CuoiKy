package com.example.demo_cuoiky;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        bottomNavigationView = findViewById(R.id.view_bottom_navigation);
        btn_add = findViewById(R.id.fab_add);

        // Load the default fragment (HomeFragment) initially
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Set up the BottomNavigationView listener to switch between fragments
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_calendar) {
                    selectedFragment = new CalendarFragment();
                    btn_add.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.nav_list) {
                    selectedFragment = new ListFragment();
                    btn_add.setVisibility(View.GONE);
                } else if (item.getItemId() == R.id.nav_settings) {
                    selectedFragment = new SettingsFragment();
                    btn_add.setVisibility(View.GONE);
                } else if (item.getItemId() == R.id.nav_home) { // Assuming there is a home menu item
                    selectedFragment = new HomeFragment();
                    btn_add.setVisibility(View.VISIBLE);
                }

                // Replace the fragment in the container
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        // FAB click listener to add task or event
        btn_add.setOnClickListener(v -> {
            if (bottomNavigationView.getSelectedItemId() == R.id.nav_home) {
                addTask();
            } else if (bottomNavigationView.getSelectedItemId() == R.id.nav_calendar) {
                addEvent();
            }
        });
    }

    // Method to replace the current fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    // Method to show AddTaskFragment when "add task" is clicked
    private void addTask() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (homeFragment != null) {
            AddTaskFragment addTaskFragment = AddTaskFragment.newInstance(homeFragment);
            addTaskFragment.show(getSupportFragmentManager(), "addTaskFragment");
        } else {
            Log.e("MainActivity", "HomeFragment not found!");
        }
    }

    // Method to show AddEventFragment when "add event" is clicked
    private void addEvent() {
        CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (calendarFragment != null) {
            AddEventFragment addEventFragment = AddEventFragment.newInstance(calendarFragment);
            addEventFragment.show(getSupportFragmentManager(), "addEventFragment");
        } else {
            Log.e("MainActivity", "CalendarFragment not found!");
        }
    }

    // Method to create a notification channel for task reminders
    private void createNotificationChannel() {
        String channelId = "task_channel";
        String channelName = "Task Reminders";
        String channelDescription = "Notifications for task reminders";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
