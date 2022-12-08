package com.example.swmsportwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Activity_Creation extends AppCompatActivity {
    private String[] activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        activities = new String[]{"Football", "Basketball", "Running", "Swimming", "Dog walking", "Tennis"};
        Spinner s = (Spinner) findViewById(R.id.Type_of_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activities);
        s.setAdapter(adapter);


        Button confirm = (Button) findViewById(R.id.host_create_activity_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHostPage();
            }
        });
    }

    private void openHostPage() {
        Intent intent = new Intent(this, Host_Activity_Page.class);
        startActivity(intent);
    }

}