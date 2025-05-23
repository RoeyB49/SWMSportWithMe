package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctionsException;
//import com.google.firebase.functions.HttpsError;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {
    private EditText Email, Fullname, Birthdate, Password, RepeatPassword;
    private RadioGroup Gender;
    private RadioButton RB1, RB2;
    private CheckBox Football, Basketball, Swimming, Running, Tennis, Dogwalking;
    private RadioGroup ActivityType;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
    final static String DATE_FORMAT = "dd/MM/yyyy";

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
                    HashMap<String, Object> userType = new HashMap<>();
                    if (ActivityType.getCheckedRadioButtonId() != -1) {
                        if (RB2.getText().toString().equals("Joining an activity")) {
                            db.addUser(user, "Join");
                            userType.put("Type", "Join");
                        } else {
                            db.addUser(user, "Host");
                            userType.put("Type", "Host");
                        }
                        userType.put("Email", Email.getText().toString());
                        db.addUser(userType, "Users");
                    }
                    openMainpage();
                }
            }

        });
    }

//    private void createAccount(String email, String password) {
//        // [START create_user_with_email]
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(Registration.this, "Authentication failed.",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//        // [END create_user_with_email]
//    }

    private void createAccount(String email, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);

        mFunctions
                .getHttpsCallable("createUser")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                String errorCode = String.valueOf(ffe.getCode());
                                String errorMessage = ffe.getMessage();
                                Log.e("error", errorCode + " - " + errorMessage);
                            }
                            return "";
                        }
                        return (String) task.getResult().getData();
                    }
                });
    }

    private void openMainpage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Helper functions
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

    public static boolean isDateValid(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean validateBirthdate() {

        String val = Birthdate.getText().toString().trim();
        if (val.isEmpty()) {
            Birthdate.setError("Field can not be empty");
            return false;
        } else if (!isDateValid(val)) {
            Birthdate.setError("Birth date is not valid!");
            return false;
        } else
            return true;

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

    private void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            openMainScreen();
            return true;
        }
        return false;
    }

}

