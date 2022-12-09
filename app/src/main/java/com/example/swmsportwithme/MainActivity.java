package com.example.swmsportwithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity {
    private Button register;
    private Button login;
    private EditText email, password;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Move to welcome screen
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = (EditText) findViewById(R.id.welcome_email);
                password = (EditText) findViewById(R.id.welcome_password);

                if (validateEmailAddress(email) && validatePassword(password)) {
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
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("TAG", "signInWithEmail:success");
                            System.out.println("---------------------------> signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            openHost();
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            System.out.println("---------------------------> signInWithEmail:failure");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.signOut();
    }

    private void openRegistrationScreen() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    private void openHost() {
        Intent intent = new Intent(this, Host_Activity_Page.class);
        startActivity(intent);
    }


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
        if (!PasswordInput.isEmpty()) {
            return true;
        } else {
            Toast.makeText(this, "Passwords does not match or too short, try again!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}

