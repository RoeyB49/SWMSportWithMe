package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Host_Activity_Page extends AppCompatActivity {
    private TextView hostName;
    private Spinner activitiesSpinner;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_page);
        hostName = (TextView) findViewById(R.id.host_name);
        hostName.setText(user.getEmail());

        setOngoingActivities();

        Button create = (Button) findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreation();
            }
        });

        Button delete = (Button) findViewById(R.id.delete_activity);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitiesSpinner.getSelectedItem();
                String selectedActivity = activitiesSpinner.getSelectedItem().toString();
                String[] strArr = selectedActivity.replaceAll(" ", "").split(",");
                if (!selectedActivity.equals("Ongoing activities")) {
                    deleteActivity(strArr);
                    setOngoingActivities();
                }

            }
        });
    }

    private void deleteActivity(String[] strArr) {
        db.collection("Host").document(user.getEmail().toString()).collection("Activities").document(strArr[0]).delete();
//        db.collection("Join").document(user.getEmail().toString()).collection("Activities").document(strArr[0]).delete();


        // delete activity at Activities collection
        CollectionReference activities = db.collection("Activities");
        activities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Activity name").equals(strArr[0]) && document.getString("Date").equals(strArr[1]) && document.getString("Time").equals(strArr[2])) {

                            CollectionReference participants = document.getReference().collection("Participants");
                            participants.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            deleteFromJoin(doc.getId(), strArr);
                                            sendEmail(doc.getId(), strArr);
                                            doc.getReference().delete();
                                        }
                                    }
                                }
                            });
                            document.getReference().collection("Participants");
                            document.getReference().delete();
                            document.getReference().delete();
                        }
                    }
                }
            }
        });
    }

    private void sendEmail(String sendTo, String[] strArr) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
        email.putExtra(Intent.EXTRA_SUBJECT, "SWM - Activity Deletion");
        email.putExtra(Intent.EXTRA_TEXT, "Activity " + strArr[0] + ", Date: " + strArr[1] + ", Time: " + strArr[2] + " is cancelled");

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void deleteFromJoin(String email, String[] strArr) {
        CollectionReference activities = db.collection("Join").document(email).collection("Activities");
        activities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Activity name").equals(strArr[0]) && document.getString("Date").equals(strArr[1]) && document.getString("Time").equals(strArr[2])) {
                            document.getReference().delete();
                        }
                    }
                }
            }
        });
    }

    private void openCreation() {
        Intent intent = new Intent(this, Activity_Creation.class);
        startActivity(intent);
    }

    private void setOngoingActivities() {
        CollectionReference subjectsRef = db.collection("Host").document(user.getEmail().toString()).collection("Activities");
        activitiesSpinner = (Spinner) findViewById(R.id.ongoing_activities);
        List<String> subjects = new ArrayList<>();
        subjects.add("Ongoing activities");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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


}