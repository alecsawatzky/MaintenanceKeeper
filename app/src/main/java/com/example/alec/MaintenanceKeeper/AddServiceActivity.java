package com.example.alec.MaintenanceKeeper;

import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddServiceActivity extends AppCompatActivity
{
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private Button btnAddService;
    private EditText etService;
    private String vehicleKey;
    private String dateChosen;
    private CalendarView calendarView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        setupActionBar();

        etService = (EditText) findViewById(R.id.etService);
        btnAddService = (Button) findViewById(R.id.btnAddService);
        calendarView = (CalendarView) findViewById(R.id.calendarView);

        vehicleKey = getIntent().getStringExtra("vehicleKey");
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference("Vehicles/" + vehicleKey + "/services");

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        dateChosen = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.YEAR));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                dateChosen = String.valueOf(i2) + "-" + String.valueOf(i1 + 1) + "-" + String.valueOf(i);
                Toast.makeText(AddServiceActivity.this, dateChosen, Toast.LENGTH_SHORT).show();
            }
        });

        // Write to the database.
        btnAddService.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               if (!etService.getText().toString().trim().equals(""))
               {
                   Service service = new Service(etService.getText().toString(), dateChosen);
                   dbReference.push().setValue(service);

                   Intent intent = new Intent(AddServiceActivity.this, ShowVehicleActivity.class);
                   intent.putExtra("id", vehicleKey);
                   startActivity(intent);
                   finish();
               }
               else 
               {
                   Toast.makeText(AddServiceActivity.this, "Please enter the Service.", Toast.LENGTH_SHORT).show();
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
            actionBar.setTitle("Add Service");
        }
    }
}
