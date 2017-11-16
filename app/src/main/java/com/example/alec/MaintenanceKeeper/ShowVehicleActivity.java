package com.example.alec.MaintenanceKeeper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
    private FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder> mFirebaseAdapter;
    private ArrayList<Service> services;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder
    {
        List<Vehicle> vehicles;
        CardView cv;
        TextView make;
        TextView model;
        ImageView vehiclePhoto;

        VehicleViewHolder(View itemView)
        {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            make = (TextView) itemView.findViewById(R.id.tvMake);
            model = (TextView) itemView.findViewById(R.id.tv_model);
            vehiclePhoto = (ImageView) itemView.findViewById(R.id.PhVehicle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vehicle);
        setupActionBar();

        services = new ArrayList<Service>();
        tvMake = (TextView) findViewById(R.id.tvMake);
        tvModel = (TextView) findViewById(R.id.tvModel);
        tvYear = (TextView) findViewById(R.id.tvYear);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Vehicles");
        final DatabaseReference servicesRef = database.getReference("Services");

        servicesRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    String key = child.getKey();

                    if (getIntent().getStringExtra("id").equals(key))
                    {
                        Vehicle vehicle = child.getValue(Vehicle.class);

                        tvMake.setText(vehicle.getMake().toUpperCase());
                        tvModel.setText(vehicle.getModel().toUpperCase());
                        tvYear.setText(vehicle.getYear());

                        Iterable<DataSnapshot> servicesItems = child.child("Services").getChildren();


                        for (DataSnapshot s: servicesItems)
                        {
                            Service service = s.getValue(Service.class);

                            Toast.makeText(ShowVehicleActivity.this, service.getName(), Toast.LENGTH_SHORT).show();

                            services.add(service);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            actionBar.setTitle("Vehicle Details");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}


//
//                    servicesRef.orderByKey().addValueEventListener(new ValueEventListener()
//                    {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot)
//                        {
//                            for (DataSnapshot child: dataSnapshot.getChildren())
//                            {
//                                Service service = child.getValue(Service.class);
//
//                                //Toast.makeText(ShowVehicleActivity.this, service.getName(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError)
//                        {
//
//                        }
//                    });

//                    Query servicesQuery = database.getReference().child("Services");
//
//                    Object o = servicesQuery.orderByKey().equals(key);
//
//                    int i =0;