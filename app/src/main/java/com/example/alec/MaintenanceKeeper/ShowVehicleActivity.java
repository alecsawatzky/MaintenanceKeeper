package com.example.alec.MaintenanceKeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowVehicleActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{
    private TextView tvMake;
    private TextView tvModel;
    private TextView tvYear;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Service> services;
    private List<String> keys;
    private ListView listView;
    private ItemAdapter adapter;
    private FirebaseDatabase database;
    private String vehicleKey;
    private Button removeVehicle;
    private SharedPreferences sharedPreferences;
    private int fontColor;
    private int fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vehicle);
        setupActionBar();

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);
        services = new ArrayList<Service>();
        keys = new ArrayList<String>();
        tvMake = (TextView) findViewById(R.id.tvMake);
        tvModel = (TextView) findViewById(R.id.tvModel);
        tvYear = (TextView) findViewById(R.id.tvYear);
        listView = (ListView) findViewById(R.id.list_view);
        removeVehicle = (Button) findViewById(R.id.btnRemoveVehicle);
        database = FirebaseDatabase.getInstance();

        removeVehicle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ShowVehicleActivity.this, AddServiceActivity.class);
                intent.putExtra("vehicleKey", vehicleKey);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Error", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu._show_vehicle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.remove_vehicle_menu:
                database.getReference("Vehicles/" + getIntent().getStringExtra("id")).removeValue();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Vehicle Details");
        }
    }

    // An adapter to link the Array List to the ListView.
    private class ItemAdapter extends ArrayAdapter<Service> {

        private ArrayList<Service> items;

        public ItemAdapter(Context context, int textViewResourceId, ArrayList<Service> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            Service o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                Button removeService = (Button) v.findViewById(R.id.btnRemoveService);

                removeService.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        database.getReference("Vehicles/" + vehicleKey+ "/services/" + keys.get(position).toString()).removeValue();
                        keys.remove(position);
                        services.clear();
                        onResume();
                    }
                });

                if (tt != null) {
                    tt.setText(o.getName());
                    tt.setTextColor(fontColor);
                    tt.setTextSize(fontSize);
                }
                if (bt != null) {
                    bt.setText("Service Date: "+ o.getDate());
                    bt.setTextSize(fontSize - 4);
                }
            }
            return v;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        DatabaseReference myRef = database.getReference("Vehicles");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();

                    if (getIntent().getStringExtra("id").equals(key))
                    {
                        Vehicle vehicle = child.getValue(Vehicle.class);
                        vehicleKey = child.getKey();

                        tvMake.setText(vehicle.getMake().toUpperCase());
                        tvModel.setText(vehicle.getModel().toUpperCase());
                        tvYear.setText(vehicle.getYear());

                        tvMake.setTextColor(fontColor);
                        tvModel.setTextColor(fontColor);
                        tvYear.setTextColor(fontColor);

                        Iterable<DataSnapshot> servicesItems = child.child("services").getChildren();

                        for (DataSnapshot s: servicesItems)
                        {
                            Service service = s.getValue(Service.class);

                            keys.add(s.getKey());
                            services.add(service);
                        }
                    }
                }

                adapter = new ItemAdapter(ShowVehicleActivity.this, R.id.list_view, services);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // An unresolvable error has occurred and Google APIs (including Sign-In) will not
                // be available.
                Log.d("Error", "onDataChangeFailed:" + databaseError);
                Toast.makeText(ShowVehicleActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
            }
        });

        String fontSizeText = sharedPreferences.getString("fontSize", "Medium");

        switch (fontSizeText)
        {
            case "Small":
                fontSize = 15;
                break;
            case "Medium":
                fontSize = 17;
                break;
            case "Large":
                fontSize = 20;
                break;
        }

        String fontColorText = sharedPreferences.getString("fontColor", "Black").toLowerCase();

        switch (fontColorText)
        {
            case "white":
                fontColor = Color.WHITE;
                break;
            case "blue":
                fontColor = Color.BLUE;
                break;
            case "red":
                fontColor = Color.RED;
                break;
            case "gray":
                fontColor = Color.GRAY;
                break;
        }

        adapter = new ItemAdapter(ShowVehicleActivity.this, R.id.list_view, services);
        listView.setAdapter(adapter);
    }
}
