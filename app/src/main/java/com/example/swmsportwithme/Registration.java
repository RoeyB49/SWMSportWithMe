package com.example.swmsportwithme;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {
    private Button next;
    private EditText Email;
    private EditText Fullname;
    private EditText Birthdate;
    private EditText Password;
    private RadioGroup Gender;
    private RadioButton RB1;
    private RadioButton RB2;
    private CheckBox Football;
    private CheckBox Basketball;
    private CheckBox Swimming;
    private CheckBox Running;
    private CheckBox Tennis;
    private CheckBox Dogwalking;
    private RadioGroup ActivityType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fullname = (EditText) findViewById(R.id.Full_Name_Registration);
                Email = (EditText) findViewById(R.id.Email_Registration);
                Password = (EditText) findViewById(R.id.Password_Registration);
                Birthdate = (EditText) findViewById(R.id.Birth_Date_Registration);
                Football = (CheckBox) findViewById(R.id.Swimming_Registration);
                Basketball = (CheckBox) findViewById(R.id.Basketball_Registration);
                Swimming = (CheckBox) findViewById(R.id.Swim_Registration);
                Running = (CheckBox) findViewById(R.id.Running_Registration);
                Dogwalking = (CheckBox) findViewById(R.id.DogWalk_Registration);
                Tennis = (CheckBox) findViewById(R.id.Tennis_Registration);
                Gender = (RadioGroup) findViewById(R.id.Genders);
                int selectedGenderId = Gender.getCheckedRadioButtonId();
                RB1 = (RadioButton) findViewById(selectedGenderId);
                ActivityType = (RadioGroup) findViewById(R.id.Activity_JoinOrHost);
                int selectedActivityId = ActivityType.getCheckedRadioButtonId();
                RB2 = (RadioButton) findViewById(selectedActivityId);
                FirebaseFirestore db=FirebaseFirestore.getInstance();;
                Map<String, Object> user = new HashMap<>();
                user.put("Birth date", Birthdate.getText().toString());
                user.put("Email",Email.getText().toString() );
                user.put("Full name",Fullname.getText().toString());
                user.put("Gender",RB1.getText().toString());
                if(Football.isChecked()) {
                    user.put("Hobbies", "Football");
                }

                if(Basketball.isChecked()) {
                    user.put("Hobbies", "Basketball");
                }

                if(Swimming.isChecked()) {
                    user.put("Hobbies", "Swimming");
                }

                if(Running.isChecked()) {
                    user.put("Hobbies", "Running");
                }

                if(Dogwalking.isChecked()) {
                    user.put("Hobbies", "Dogwalking");
                }

                if(Tennis.isChecked()) {
                    user.put("Hobbies", "Tennis");
                }
                user.put("Password",Password.getText().toString());
                // Add a new document with a generated ID
                if(RB2.getText().toString().equals("Hosting an activity")){
                    db.collection("Host")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
                if(RB2.getText().toString().equals("Joining an activity")){
                    db.collection("Join")
                            .add(user)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }

                openMainpage();
            }
        });
    }



    private void openMainpage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
