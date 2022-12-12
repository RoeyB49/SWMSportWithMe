package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Activity_Guest_Page extends AppCompatActivity {
    private TextView guestName;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    //    private Spinner activitiesSpinner;
    private Spinner ongoingSpinner;
    private String[] activities;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseRef dbRef = new FirebaseRef();
    private Button joinActivity;

    private TextView textview;
    private ArrayList<String> arrayList;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_page);
        guestName = (TextView) findViewById(R.id.guest_name);
        guestName.setText(user.getEmail());

        activities = new String[]{"Choose an activity", "Football", "Basketball", "Running", "Swimming", "Dogwalking", "Tennis"};
//        activitiesSpinner = (Spinner) findViewById(R.id.spinner3);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activities);
//        activitiesSpinner.setAdapter(adapter);

        setOngoingActivities();

        // assign variable
        textview = findViewById(R.id.testView);

        // initialize array list
        arrayList = new ArrayList<>();
        setAvailableActivities();

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog = new Dialog(Activity_Guest_Page.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(650, 800);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_Guest_Page.this, android.R.layout.simple_list_item_1, arrayList);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        textview.setText(adapter.getItem(position));

                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });


        joinActivity = findViewById(R.id.join_new_activity);
        joinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String selectedActivity = activitiesSpinner.getSelectedItem().toString();
                String selectedActivity = textview.getText().toString();
                if (!selectedActivity.equals("")) {
                    String[] strArr = selectedActivity.replaceAll(" ", "").split(",");

                    Map<String, Object> activity = new HashMap<>();
                    activity.put("Activity name", strArr[0]);
                    activity.put("Date", strArr[1]);
                    activity.put("Time", strArr[2]);
                    dbRef.addJoinActivities(activity);

                    setOngoingActivities();


                    updateJoinedUsers(strArr);
                }
            }
        });
    }

    private void updateJoinedUsers(String[] strArr) {
        CollectionReference activities = db.collection("Activities");
        activities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Activity name").equals(strArr[0]) && document.getString("Date").equals(strArr[1]) && document.getString("Time").equals(strArr[2])) {
                            Map<String, Object> joined = new HashMap<>();
                            joined.put("Joined", true);
                            document.getReference().collection("Participants").document(user.getEmail()).set(joined);
                        }
                    }
                }
            }
        });
    }

    private void setAvailableActivities() {

        CollectionReference subjectsRef = db.collection("Activities");
//        activitiesSpinner = (Spinner) findViewById(R.id.spinner3);
//        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Activity name");
                        subject += ", " + document.getString("Date");
                        subject += ", " + document.getString("Time");
                        arrayList.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
//        activitiesSpinner.setAdapter(adapter);
    }


    private void setOngoingActivities() {
        CollectionReference subjectsRef = db.collection("Join").document(user.getEmail().toString()).collection("Activities");
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
