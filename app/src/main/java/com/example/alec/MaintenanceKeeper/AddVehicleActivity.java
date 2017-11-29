package com.example.alec.MaintenanceKeeper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

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

        Calendar calendar = Calendar.getInstance();

        etYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        // Write to the database.
        btnAddVehicle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!etMake.getText().toString().trim().equals("") && !etModel.getText().toString().trim().equals("") && !etYear.getText().toString().trim().equals(""))
                {
                    Vehicle vehicle = new Vehicle(etMake.getText().toString(), etModel.getText().toString(), etYear.getText().toString());
                    dbReference.push().setValue(vehicle);
                    finish();
                }
                else
                {
                    Toast.makeText(AddVehicleActivity.this, "Please enter a Make, Model and Year.", Toast.LENGTH_SHORT).show();
                }
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
