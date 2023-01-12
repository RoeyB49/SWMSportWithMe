package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button register;
    private Button login;
    public EditText email, password;
    FirebaseRef db = new FirebaseRef();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Built in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register = (Button) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);


        // Move to registration screen
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistrationScreen();
            }
        });

        // Move to user(Host or Guest) welcome screen and validate that the fields are correctly filled with the helper functions we created

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = (EditText) findViewById(R.id.welcome_email);
                password = (EditText) findViewById(R.id.welcome_password);
                if (validateEmailAddress(email) && validatePassword(password)) {
                    //Here we send the Email and the Password entered to helper function
                    signIn(email.getText().toString(), password.getText().toString());

                }
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserType("Users", email);
                        } else {
                            System.out.println("---------------------------> signInWithEmail:failure");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //This function helps us to prepare correctly the welcome page for each of the users logged on(Host or Guest)
    public void getUserType(String collectionPath, String email) {
        DocumentReference docRef = db.db.collection(collectionPath).document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if (Objects.equals(document.get("Type"), "Join")) {
                            openJoin();
                        } else if (Objects.equals(document.get("Type"), "Host")) {
                            openHost();
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void openRegistrationScreen() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    private void openHost() {
        Intent intent = new Intent(this, Host_Activity_Page.class);
        startActivity(intent);
    }

    private void openJoin() {
        Intent intent = new Intent(this, Activity_Guest_Page.class);
        startActivity(intent);
    }

    private boolean validateEmailAddress(EditText Email) {
        String emailInput = Email.getText().toString();
        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            return true;
        } else {
            Toast.makeText(this, "Invalid Email please try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validatePassword(EditText Password) {
        String PasswordInput = Password.getText().toString();
        if (!PasswordInput.isEmpty()) {
            return true;
        } else {
            Toast.makeText(this, "Passwords does not match or too short, try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

