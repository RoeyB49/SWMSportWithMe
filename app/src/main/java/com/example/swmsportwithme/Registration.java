package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {
    private EditText Email, Fullname, Birthdate, Password, RepeatPassword;
    private RadioGroup Gender;
    private RadioButton RB1, RB2;
    private CheckBox Football, Basketball, Swimming, Running, Tennis, Dogwalking;
    private RadioGroup ActivityType;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private boolean validateEmailAddress(EditText Email) {
        String emailInput = Email.getText().toString();
        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(this, "Congratulations! you have successfully signed up for SWM", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Invalid Email please try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validatePassword(EditText Password) {
        String PasswordInput = Password.getText().toString();
        if (!PasswordInput.isEmpty() && (Password.getText().toString().equals(RepeatPassword.getText().toString())) && PasswordInput.length() > 5) {
            return true;
        } else {
            Toast.makeText(this, "Passwords does not match or too short, try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateFullName() {
        String val = Fullname.getText().toString().trim();
        if (val.isEmpty()) {
            Fullname.setError("Field can not be empty");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateBirthdate() {
        String val = Birthdate.getText().toString().trim();
        if (val.isEmpty()) {
            Birthdate.setError("Field can not be empty");
            return false;
        } else {
            return true;
        }
    }

    private boolean CheckPassword() {
        String val = Password.getText().toString().trim();
        if (val.isEmpty()) {
            Password.setError("Field can not be empty");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRepeatPassword() {
        String val = RepeatPassword.getText().toString().trim();
        if (val.isEmpty()) {
            RepeatPassword.setError("Field can not be empty");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateGender() {
        Gender = (RadioGroup) findViewById(R.id.Genders);
        if (Gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select a Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateActivityJoinOrHost() {
        ActivityType = (RadioGroup) findViewById(R.id.Activity_JoinOrHost);
        if (ActivityType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please make your choice to be a Host or a Guest.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean validateHobbies() {

        if (Football.isChecked() || Basketball.isChecked() || Swimming.isChecked() || Running.isChecked() || Dogwalking.isChecked() || Tennis.isChecked()) {
            return true;
        } else {
            Toast.makeText(this, "You have to choose at least one hobby to proceed!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

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
                RepeatPassword = (EditText) findViewById(R.id.Repeat_Password_Registration);
                Birthdate = (EditText) findViewById(R.id.Birth_Date_Registration);
                Football = (CheckBox) findViewById(R.id.football_Registration);
                Basketball = (CheckBox) findViewById(R.id.Basketball_Registration);
                Swimming = (CheckBox) findViewById(R.id.Swim_Registration);
                Running = (CheckBox) findViewById(R.id.Running_Registration);
                Dogwalking = (CheckBox) findViewById(R.id.DogWalk_Registration);
                Tennis = (CheckBox) findViewById(R.id.Tennis_Registration);
                Gender = (RadioGroup) findViewById(R.id.Genders);
                if (Gender.getCheckedRadioButtonId() != -1) {
                    int selectedGenderId = Gender.getCheckedRadioButtonId();
                    RB1 = (RadioButton) findViewById(selectedGenderId);
                }
                ActivityType = (RadioGroup) findViewById(R.id.Activity_JoinOrHost);
                if (ActivityType.getCheckedRadioButtonId() != -1) {
                    int selectedActivityId = ActivityType.getCheckedRadioButtonId();
                    RB2 = (RadioButton) findViewById(selectedActivityId);
                }
                if (validateActivityJoinOrHost() && validateGender() && validatePassword(Password) && validateFullName() && validateBirthdate() && CheckPassword() && validateRepeatPassword() &&
                        validateHobbies() && validateEmailAddress(Email)) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("Email", Email.getText().toString());
                    user.put("Password", Password.getText().toString());
//                    createAccount((String)user.get("Email"), (String)user.get("Password"));
                    user.put("Birth date", Birthdate.getText().toString());
                    user.put("Full name", Fullname.getText().toString());
                    if (Gender.getCheckedRadioButtonId() != -1) {
                        user.put("Gender", RB1.getText().toString());
                    }
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

                    createAccount((String) user.get("Email"), (String) user.get("Password"));

                    // Add a new document with a generated ID
                    FirebaseRef db = new FirebaseRef();

                    if (ActivityType.getCheckedRadioButtonId() != -1) {
                        if (RB2.getText().toString().equals("Joining an activity")) {
                            db.addUser(user, "Join");
                        } else {
                            db.addUser(user, "Host");
                        }
                    }
                    openMainpage();
                }
            }

        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void openMainpage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

