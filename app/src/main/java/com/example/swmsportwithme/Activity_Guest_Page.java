package com.example.swmsportwithme;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Activity_Guest_Page extends AppCompatActivity {
    private TextView guestName;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private Spinner ongoingSpinner;
    protected FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseRef dbRef = new FirebaseRef();
    private Button joinActivity;

    private Button DeleteActivity;
    private TextView textview;
    private ArrayList<String> arrayList;
    //Object that will help us with the search function for the Guest user
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Build in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_page);
        setOngoingActivities();

//Start of the search function

        // assign variable for the search option
        textview = findViewById(R.id.testView);

        // initialize array list
        arrayList = new ArrayList<>();
        setAvailableActivities();

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog = new Dialog(Activity_Guest_Page.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(650, 800);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity_Guest_Page.this, android.R.layout.simple_list_item_1, arrayList);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        textview.setText(adapter.getItem(position));

                        // Dismiss dialog
                        dialog.dismiss();
                    }
                });
            }
        });
//End of the search function
        joinActivity = findViewById(R.id.join_new_activity);
        joinActivity.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String selectedActivity = textview.getText().toString();
                if (!selectedActivity.equals("")) {
                    //Helper array to make it easier for us to make activity object for the database
                    String[] strArr = selectedActivity.replaceAll(" ", "").split(",");
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("Activity name", strArr[0]);
                    activity.put("Date", strArr[1]);
                    activity.put("Time", strArr[2]);
                    dbRef.addJoinActivities(activity);
//Update the bottom spinner on which activity did the user signed
                    setOngoingActivities();
//Calling this function will put the activity the user chose to join with the helper array we created to a document called participants with his mail
                    updateJoinedUsers(strArr);
                }
            }
        });
        DeleteActivity = findViewById(R.id.delete_activity_button_join);
        DeleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ongoingSpinner.getSelectedItem();
                String selectedActivity = ongoingSpinner.getSelectedItem().toString();
                String[] strArr = selectedActivity.replaceAll(" ", "").split(",");
                if (!selectedActivity.equals("Ongoing activities")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Guest_Page.this);
                    builder.setMessage("The activity will be deleted, are you sure?");
                    builder.setTitle("Confirm Deletion");
                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(true);
                    // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                    builder.setPositiveButton("Confirm", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // When the user click Confirm Button, the activity will be deleted
                        deleteactivity(strArr);
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
            }
        });
    }

    private void updateJoinedUsers(String[] strArr) {
        CollectionReference activities = db.collection("Activities");
        activities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Activity name").equals(strArr[0]) && document.getString("Date").equals(strArr[1]) && document.getString("Time").equals(strArr[2])) {
                            Map<String, Object> joined = new HashMap<>();
                            joined.put("Joined", true);
                            document.getReference().collection("Participants").document(user.getEmail()).set(joined);
                        }
                    }
                }
            }
        });
    }
//Update the available activities from the database to the search function we created on top
    private void setAvailableActivities() {
        CollectionReference subjectsRef = db.collection("Activities");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Activity name");
                        subject += ", " + document.getString("Date");
                        subject += ", " + document.getString("Time");
                        arrayList.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //Set the Ongoing activities of the user from the database
    private void setOngoingActivities() {
        CollectionReference subjectsRef = db.collection("Join").document(user.getEmail().toString()).collection("Activities");
        ongoingSpinner = (Spinner) findViewById(R.id.ongoing_spinner);
        List<String> subjects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("Activity name");
                        subject += ", " + document.getString("Date");
                        subject += ", " + document.getString("Time");
                        subjects.add(subject);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        ongoingSpinner.setAdapter(adapter);
    }
    private void deleteactivity(String[] strArr) {
        CollectionReference activities = db.collection("Join").document(user.getEmail().toString()).collection("Activities");
        activities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("Activity name").equals(strArr[0]) && document.getString("Date").equals(strArr[1]) && document.getString("Time").equals(strArr[2])) {
                            document.getReference().delete();
                        }
                    }
                    setOngoingActivities();
                }
            }
        });
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
