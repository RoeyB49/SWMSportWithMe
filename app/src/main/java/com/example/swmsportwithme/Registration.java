package com.example.swmsportwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Registration extends AppCompatActivity {
    private EditText Email, Fullname, Birthdate, Password, RepeatPassword;
    private RadioGroup Gender;
    private RadioButton RB1, RB2;
    private CheckBox Football, Basketball, Swimming, Running, Tennis, Dogwalking;
    private RadioGroup ActivityType;


    private boolean validateEmailAddress(EditText Email) {
    String emailInput = Email.getText().toString();
    if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
        Toast.makeText(this, "Email Validate Successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }else{
        Toast.makeText(this, "Invalid Email please try again!", Toast.LENGTH_SHORT).show();
        return false;
    }
}
    private boolean validatePassword(EditText Password) {
        String PasswordInput = Password.getText().toString();
        if (!PasswordInput.isEmpty() && (Password.getText().toString().equals(RepeatPassword.getText().toString()))){
            return true;
        }else{
            Toast.makeText(this, "One of the passwords does not match to the other, try again!", Toast.LENGTH_SHORT).show();
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
        if (Gender.getCheckedRadioButtonId()==-1) {
            Toast.makeText(this, "Please Select a Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateActivityJoinOrHost() {
        ActivityType = (RadioGroup)findViewById(R.id.Activity_JoinOrHost);
        if (ActivityType.getCheckedRadioButtonId()==-1) {
            Toast.makeText(this, "Please make your choice to be a Host or a Guest.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean validateHobbies() {

        if (Football.isChecked()||Basketball.isChecked()||Swimming.isChecked()||Running.isChecked()||Dogwalking.isChecked()||Tennis.isChecked()) {
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
                if(validateActivityJoinOrHost()&&validateGender()&&validatePassword(Password)&&validateFullName()&&validateBirthdate()&&CheckPassword()&&validateRepeatPassword()&&
                        validateHobbies()&&validateEmailAddress(Email)) {
                    openMainpage();
                }
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
                if (Gender.getCheckedRadioButtonId()!=-1) {
                    int selectedGenderId = Gender.getCheckedRadioButtonId();
                    RB1 = (RadioButton) findViewById(selectedGenderId);
                }
                ActivityType = (RadioGroup) findViewById(R.id.Activity_JoinOrHost);
                if (ActivityType.getCheckedRadioButtonId()!=-1) {
                    int selectedActivityId = ActivityType.getCheckedRadioButtonId();
                    RB2 = (RadioButton) findViewById(selectedActivityId);
                }
                Map<String, Object> user = new HashMap<>();
                user.put("Birth date", Birthdate.getText().toString());
                user.put("Email", Email.getText().toString());
                user.put("Full name", Fullname.getText().toString());
                user.put("Password", Password.getText().toString());
                if (Gender.getCheckedRadioButtonId()!=-1) {
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


                // Add a new document with a generated ID
                FirebaseRef db = new FirebaseRef();
                if (ActivityType.getCheckedRadioButtonId()!=-1) {
                    if (RB2.getText().toString().equals("Joining an activity")) {
                        db.addUser(user, "Join");
                    } else {
                        db.addUser(user, "Host");
                    }
                }
            }
        });
    }


    private void openMainpage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
