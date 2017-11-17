package com.example.alec.MaintenanceKeeper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.key;

public class AddServiceActivity extends AppCompatActivity
{
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private Button btnAddVehicle;
    private EditText etMake;
    private EditText etModel;
    private EditText etYear;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        setupActionBar();

        String vehicleKey = getIntent().getStringExtra("vehicleKey");
        database = FirebaseDatabase.getInstance();
        dbReference= database.getReference("Vehicles/" + vehicleKey + "/services");

        //database.getReference().child("Vehicles").orderByChild("make").equalTo("GMC");




        Service service = new Service("engine tune up", "10-10-2017");
        dbReference.push().setValue(service);



    }

    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Service");
        }
    }
}
