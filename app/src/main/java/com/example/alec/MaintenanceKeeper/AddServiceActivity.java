package com.example.alec.MaintenanceKeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddServiceActivity extends AppCompatActivity
{
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private Button btnAddService;
    private EditText etService;
    private EditText etDate;
    private EditText etNotes;
    private String vehicleKey;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        setupActionBar();

        etService = (EditText) findViewById(R.id.etService);
        etDate = (EditText) findViewById(R.id.etDate);
        etNotes = (EditText) findViewById(R.id.etNotes);
        btnAddService = (Button) findViewById(R.id.btnAddService);

        vehicleKey = getIntent().getStringExtra("vehicleKey");
        database = FirebaseDatabase.getInstance();
        dbReference= database.getReference("Vehicles/" + vehicleKey + "/services");

        //database.getReference().child("Vehicles").orderByChild("make").equalTo("GMC");

//        // Write to the database.
        btnAddService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Service service = new Service(etService.getText().toString(), etDate.getText().toString(), etNotes.getText().toString());
                dbReference.push().setValue(service);

                Intent intent = new Intent(AddServiceActivity.this, ShowVehicleActivity.class);
                intent.putExtra("id", vehicleKey);
                startActivity(intent);
                finish();
            }
        });

//        Service service = new Service("engine tune up", "10-10-2017");
//        dbReference.push().setValue(service);
//
//        Intent intent = new Intent(AddServiceActivity.this, ShowVehicleActivity.class);
//        intent.putExtra("id", vehicleKey);
//        startActivity(intent);
//        finish();
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
