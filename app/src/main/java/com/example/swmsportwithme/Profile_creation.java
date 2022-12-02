package com.example.swmsportwithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Profile_creation extends AppCompatActivity {
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileCreationScreen();
            }
        });


    }

    private void openProfileCreationScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}