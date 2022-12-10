package com.example.swmsportwithme;

import static com.example.swmsportwithme.Registration.DATE_FORMAT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;

public class Activity_Creation extends AppCompatActivity {
    private String[] activities;
    private Spinner activitiesSpinner;
    EditText date, time;
    FirebaseRef db = new FirebaseRef();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        activities = new String[]{"Choose an activity", "Football", "Basketball", "Running", "Swimming", "Dog walking", "Tennis"};
        activitiesSpinner = (Spinner) findViewById(R.id.Type_of_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activities);
        activitiesSpinner.setAdapter(adapter);


        Button confirm = (Button) findViewById(R.id.host_create_activity_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if all inputs are valid
                if (validateActivity() && validateBirthdate() && validateTime()) {
                    // Add to firebase
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("Activity name", activitiesSpinner.getSelectedItem().toString());
                    activity.put("Date", date.getText().toString());
                    activity.put("Time", time.getText().toString());
                    System.out.println("----------------------------------> " + mAuth.getCurrentUser());
                    activity.put("User", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                    db.addActivity(activity);

                    openHostPage();
                }
            }
        });
    }

    private void openHostPage() {
        Intent intent = new Intent(this, Host_Activity_Page.class);
        startActivity(intent);
    }

    private boolean validateActivity() {
        if (activitiesSpinner.getSelectedItem().toString().equals("Choose an activity")) {
            Toast.makeText(this, "Please choose an activity", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public static boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private boolean validateBirthdate(){
        date = findViewById(R.id.host_activity_date);
        String val = date.getText().toString().trim();
        if (val.isEmpty()) {
            date.setError("Field can not be empty");
            return false;
        } else if(!isDateValid(val)){
            date.setError("Birth date is not valid!");
            return false;
        }else
            return true;

    }
    private boolean validateTime() {
        time = findViewById(R.id.host_activity_time);
        String val = time.getText().toString().trim();
        if (val.isEmpty()) {
            time.setError("Field can not be empty");
            return false;
        } else {
            return true;
        }
    }

}