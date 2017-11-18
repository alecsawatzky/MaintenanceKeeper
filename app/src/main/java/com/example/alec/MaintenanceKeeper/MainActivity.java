/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.alec.MaintenanceKeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener
{
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

    // FireBase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private ListView listView;
    //    private VehicleAdapter adapter;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder> mFirebaseAdapter;

    private ArrayList<Vehicle> vehicles;


    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "Vehicles";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private Button mSendButton;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar progressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;
    // Firebase instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private Vehicle vehicle;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int fontSize;
    private String fontColor;
    private int color;

    // Firebase instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);

        //listView = (ListView) findViewById(R.id.vehicle_list_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setVisibility(View.INVISIBLE);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<Vehicle> parser = new SnapshotParser<Vehicle>()
        {
            @Override
            public Vehicle parseSnapshot(DataSnapshot dataSnapshot)
            {
                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                if (vehicle != null)
                {
                    vehicle.setId(dataSnapshot.getKey());
                }
                return vehicle;
            }
        };

        DatabaseReference vehiclesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        FirebaseRecyclerOptions<Vehicle> options =
                new FirebaseRecyclerOptions.Builder<Vehicle>()
                        .setQuery(vehiclesRef, parser)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder>(options)
        {
            @Override
            public VehicleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
            {

                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new VehicleViewHolder(inflater.inflate(R.layout.card_item, viewGroup, false));
            }


            @Override
            protected void onBindViewHolder(final VehicleViewHolder viewHolder,
                                            int position,
                                            final Vehicle vehicle)
            {
                progressBar.setVisibility(ProgressBar.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                viewHolder.make.setText(vehicle.getMake());
                viewHolder.model.setText(vehicle.getModel());
                
                viewHolder.make.setTextColor(color);
                viewHolder.make.setTextSize(fontSize);
                viewHolder.model.setTextSize(fontSize / 2);

                viewHolder.cv.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(MainActivity.this, ShowVehicleActivity.class);
                        intent.putExtra("id", vehicle.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1)))
                {
                    recyclerView.scrollToPosition(0);
                }
            }
        });

        recyclerView.setAdapter(mFirebaseAdapter);


//        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        vehicles = new ArrayList<Vehicle>();
//        vehicles.add(new Vehicle("new", "new"));
//        vehicles.add(new Vehicle("new", "new"));
//
//        adapter = new VehicleAdapter(vehicles).;
//        recyclerView.setAdapter(adapter);


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null)
        {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else
        {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null)
            {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_vehicle_menu:
                startActivity(new Intent(MainActivity.this, AddVehicleActivity.class));
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            case  R.id.settings_menu:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }



//    public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
//
//        private List<Vehicle> contactList;
//
//        public VehicleAdapter(List<Vehicle> contactList) {
//            this.contactList = contactList;
//        }
//
//        @Override
//        public int getItemCount() {
//            return contactList.size();
//        }
//
//        @Override
//        public void onBindViewHolder(VehicleViewHolder contactViewHolder, int i) {
//            Vehicle ci = contactList.get(i);
//            contactViewHolder.make.setText(ci.getMake());
//        }
//
//        @Override
//        public VehicleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//            View itemView = LayoutInflater.
//                    from(viewGroup.getContext()).
//                    inflate(R.layout.card_item, viewGroup, false);
//
//            return new VehicleViewHolder(itemView);
//        }
//
//
//    }


//    // An adapter to link the Array List to the ListView.
//    private class ItemAdapter extends ArrayAdapter<Vehicle>
//    {
//
//        private ArrayList<Vehicle> items;
//
//        public ItemAdapter(Context context, int textViewResourceId, ArrayList<Vehicle> items)
//        {
//            super(context, textViewResourceId, items);
//            this.items = items;
//        }
//
//        //This method is called once for every item in the ArrayList as the list is loaded.
//        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent)
//        {
//            View v = convertView;
//            Vehicle vehicle = items.get(position);
//            if (vehicle != null)
//            {
//                TextView tt = (TextView) v.findViewById(R.id.toptext);
//                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
//                if (tt != null)
//                {
//                    tt.setText(vehicle.getMake());
//                }
//                if (bt != null)
//                {
//                    bt.setText(vehicle.getModel());
//                }
//            }
//            return v;
//        }
//    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction()
    {
        return Actions.newView("Main", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStop()
    {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }

//    public static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView messageTextView;
//        ImageView messageImageView;
//        TextView messengerTextView;
//        CircleImageView messengerImageView;
//
//        public MessageViewHolder(View v) {
//            super(v);
//            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
//            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
//            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
//            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
//        }
//    }

    private boolean  isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mFirebaseAdapter.startListening();

        if (!isNetworkConnected())
        {
            Toast.makeText(this, "No Network Connection Found.", Toast.LENGTH_LONG).show();
        }

        String fontSizeText = sharedPreferences.getString("fontSize", "Medium");

        switch (fontSizeText)
        {
            case "Small":
                fontSize = 20;
                break;
            case "Medium":
                fontSize = 30;
                break;
            case "Large":
                fontSize = 40;
                break;
        }

        fontColor = sharedPreferences.getString("fontColor", "Black").toLowerCase();

        switch (fontColor)
        {
            case "white":
                color = Color.WHITE;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "red":
                color = Color.RED;
                break;
            case "gray":
                color = Color.GRAY;
                break;
        }


//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Vehicles");
//
//        vehicles = new ArrayList<Vehicle>();
//
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot)
//            {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                vehicles.clear();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
//                {
//                    Vehicle vehicle = postSnapshot.getValue(Vehicle.class);
//                    vehicles.add(vehicle);
//
//                }
//                //adapter = new ItemAdapter(MainActivity.this, R.id.card_view, vehicles);
//
//                //listView.setAdapter(adapter);
//
//
//
//
//
//                //progressBar.setVisibility(ProgressBar.INVISIBLE);
//            }
//
//
//
//
//            public void onCancelled(DatabaseError databaseError)
//            {
//
//            }
//        });
//    }
//
//




    }
}
