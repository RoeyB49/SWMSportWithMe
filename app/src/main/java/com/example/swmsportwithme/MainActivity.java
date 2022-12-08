package com.example.swmsportwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button register;
    private Button login;

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

    }

    private void openRegistrationScreen() {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }
}