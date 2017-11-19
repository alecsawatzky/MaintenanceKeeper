package com.example.alec.MaintenanceKeeper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddVehicleActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_add_vehicle);
        setupActionBar();

        database = FirebaseDatabase.getInstance();
        dbReference= database.getReference("Vehicles");

        btnAddVehicle = (Button) findViewById(R.id.btnAddVehicle);
        etMake = (EditText) findViewById(R.id.etMake);
        etModel = (EditText) findViewById(R.id.etModel);
        etYear = (EditText) findViewById(R.id.tvYear);

        // Write to the database.
        btnAddVehicle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Vehicle vehicle = new Vehicle(etMake.getText().toString(), etModel.getText().toString(), etYear.getText().toString());
                dbReference.push().setValue(vehicle);
                finish();
            }
        });
    }

    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Vehicle");
        }
    }
}
