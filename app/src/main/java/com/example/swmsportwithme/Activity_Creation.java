package com.example.swmsportwithme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Activity_Creation extends AppCompatActivity {
    private String[] activities;
    private Spinner activitiesSpinner;
    //    EditText time;
    FirebaseRef db = new FirebaseRef();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // on below line we are creating variables.
    private Button date;
    private Button time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Build in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        activities = new String[]{"Choose an activity", "Football", "Basketball", "Running", "Swimming", "Dogwalking", "Tennis"};
        activitiesSpinner = (Spinner) findViewById(R.id.Type_of_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activities);
        activitiesSpinner.setAdapter(adapter);

        // Date Picker
        date = findViewById(R.id.idBtnPickDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        Activity_Creation.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear,
                                                  int selectedMonth, int selectedDay) {
                                // on below line we are setting date to our button.
                                if (selectedYear >= year && selectedMonth >= month && selectedDay >= day) {
                                    date.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
                                }
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        // Time Picker
        time = findViewById(R.id.idBtnPickTime);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting the
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Creation.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour,
                                                  int selectedMinute) {
                                // on below line we are setting selected time
                                // in our text view.
                                time.setText(selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });


        Button confirm = (Button) findViewById(R.id.host_create_activity_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check if all inputs are valid
                if (validateActivity() && validatedate() && validateTime()) {
                    // Add to firebase
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("Activity name", activitiesSpinner.getSelectedItem().toString());
                    activity.put("Date", date.getText().toString());
                    activity.put("Time", time.getText().toString());
                    activity.put("User", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                    db.addActivity(activity);
                    db.addHostActivities(activity);
                    openHostPage();
                }
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Creation.this);
                builder.setMessage("Discard Changes and go back?");
                builder.setTitle("Unsaved Changes");
                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(true);
                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Confirm", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // When the user click Confirm Button, the activity will be deleted
                    openHostPage();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // The deletion was cancelled
                    dialog.cancel();
                });
                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }

    private void openHostPage() {
        Intent intent = new Intent(this, Host_Activity_Page.class);
        startActivity(intent);
    }

    private boolean validateActivity() {
        if (activitiesSpinner.getSelectedItem().toString().equals("Choose an activity")) {
            Toast.makeText(this, "Please choose an activity", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validatedate() {
        String val = date.getText().toString().trim();
        if (val.equals("Pick Date")) {
            date.setError("Please choose date");
            Toast.makeText(this, "Please choose date", Toast.LENGTH_LONG).show();
            return false;
        } else {
            date.setError(null);
            return true;
        }

    }

    private boolean validateTime() {
        String val = time.getText().toString().trim();
        if (val.equals("Pick Time")) {
            time.setError("Please choose time");
            Toast.makeText(this, "Please choose time", Toast.LENGTH_LONG).show();
            return false;
        } else {
            time.setError(null);
            return true;
        }
    }

}