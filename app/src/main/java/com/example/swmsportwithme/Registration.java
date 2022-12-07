package com.example.swmsportwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {
    private EditText Email, Fullname, Birthdate, Password;
    private RadioGroup Gender;
    private RadioButton RB1, RB2;
    private CheckBox Football, Basketball, Swimming, Running, Tennis, Dogwalking;
    private RadioGroup ActivityType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fullname = (EditText) findViewById(R.id.Full_Name_Registration);
                Email = (EditText) findViewById(R.id.Email_Registration);
                Password = (EditText) findViewById(R.id.Password_Registration);
                Birthdate = (EditText) findViewById(R.id.Birth_Date_Registration);

                Football = (CheckBox) findViewById(R.id.football_Registration);
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

                Map<String, Object> user = new HashMap<>();
                user.put("Birth date", Birthdate.getText().toString());
                user.put("Email", Email.getText().toString());
                user.put("Full name", Fullname.getText().toString());
                user.put("Gender", RB1.getText().toString());

                user.put("Password", Password.getText().toString());

                ArrayList<String> hobbiesArr = new ArrayList<>();
                if (Football.isChecked()) {
                    hobbiesArr.add("Football");
                }
                if (Basketball.isChecked()) {
                    hobbiesArr.add("Basketball");
                }
                if (Swimming.isChecked()) {
                    hobbiesArr.add("Swimming");
                }
                if (Running.isChecked()) {
                    hobbiesArr.add("Running");
                }
                if (Dogwalking.isChecked()) {
                    hobbiesArr.add("Dog walking");
                }
                if (Tennis.isChecked()) {
                    hobbiesArr.add("Tennis");
                }
                user.put("Hobbies", hobbiesArr);


                // Add a new document with a generated ID
                FirebaseRef db = new FirebaseRef();
                if (RB2.getText().toString().equals("Joining an activity")) {
                    db.addUser(user, "Join");
                } else {
                    db.addUser(user, "Host");
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
