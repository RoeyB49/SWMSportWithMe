package com.example.swmsportwithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Guest_Page extends AppCompatActivity {
    private TextView guestName;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private Spinner activitiesSpinner;
    private Spinner ongoingSpinner;
    private String[] activities;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseRef dbRef = new FirebaseRef();
    Button joinActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_page);
        guestName = (TextView) findViewById(R.id.guest_name);
        guestName.setText(user.getEmail());

        activities = new String[]{"Choose an activity", "Football", "Basketball", "Running", "Swimming", "Dog walking", "Tennis"};
        activitiesSpinner = (Spinner) findViewById(R.id.Available_Activities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activities);
        activitiesSpinner.setAdapter(adapter);
        setAvailableActivities();
        setOngoingActivities();


        joinActivity = findViewById(R.id.join_new_activity);
        joinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedActivity = activitiesSpinner.getSelectedItem().toString();
                String[] strArr = selectedActivity.trim().split(",");

                Map<String, Object> activity = new HashMap<>();
                activity.put("Activity name", strArr[0]);
                activity.put("Date", strArr[1]);
                activity.put("Time", strArr[2]);
                dbRef.addJoinActivities(activity);

                setOngoingActivities();
            }
        });

    }

    private void setAvailableActivities() {

        CollectionReference subjectsRef = db.collection("Activities");
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        activitiesSpinner = (Spinner) findViewById(R.id.Available_Activities);
        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        activitiesSpinner.setAdapter(adapter);
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Activity name");
                        subject += ", " + document.getString("Date");
                        subject += ", " + document.getString("Time");
                        subjects.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        activitiesSpinner.setAdapter(adapter);
    }



    private void setOngoingActivities() {
        //        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference subjectsRef = db.collection("Join").document(user.getEmail().toString()).collection("Activities");
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ongoingSpinner = (Spinner) findViewById(R.id.ongoing_spinner);
        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        activitiesSpinner.setAdapter(adapter);
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Activity name");
                        subject += ", " + document.getString("Date");
                        subject += ", " + document.getString("Time");
                        subjects.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        ongoingSpinner.setAdapter(adapter);
    }


}
