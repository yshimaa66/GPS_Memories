package com.example.gps_memories.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.gps_memories.NotificationService;
import com.example.gps_memories.R;
import com.example.gps_memories.controller.Memory.AddActivity;
import com.example.gps_memories.controller.Memory.ShowActivity;
import com.example.gps_memories.controller.Memory.View_MemoryActivity;
import com.example.gps_memories.controller.Register_Login.MainActivity;
import com.example.gps_memories.model.Memory_Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {


    protected FloatingActionButton showbtn;
    protected FloatingActionButton addbtn;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    FirebaseUser firebaseUser;

    List<Memory_Model> memory_models;


    boolean notify = true;

    public static LatLng latLngg;

    public static String currentuserid;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        currentuserid=firebaseUser.getUid();

        memory_models = new ArrayList<>();



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        initView();




        showbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ShowActivity.class);


                startActivity(intent);


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, AddActivity.class);


                startActivity(intent);


                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });





    }



    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.home_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {


            Intent intent = new Intent(HomeActivity.this, MainActivity.class);

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));

            startActivity(intent);

            finish();


        }


        return super.onOptionsItemSelected(item);
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker



                MarkerOptions markerOptions = new MarkerOptions();

                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

               latLngg = latLng;

                //move map camera



                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Memories");

                reference.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            Memory_Model memory_model = snapshot.getValue(Memory_Model.class);


                            if (memory_model != null && memory_model.getUserid().equals(firebaseUser.getUid())) {

                                LatLng latLngo = new LatLng(memory_model.getLatitude(), memory_model.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLngo);
                                markerOptions.title(memory_model.getTitle() + "    Memory's time : " + memory_model.getTime());
                                //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


                                /*if (Math.ceil(latLngo.latitude) == Math.ceil(latLng.latitude)) {

                                    Intent intent;


                                    intent = new Intent(getApplicationContext(), View_MemoryActivity.class);
                                    intent.putExtra("memory_id",memory_model.getId());


                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);

                                    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.ic_marker)
                                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                                    R.mipmap.ic_launcher))
                                            .setContentTitle("You have a memory here")
                                            .setContentText("Have a look to bring memories back")
                                            .setAutoCancel(true)
                                            .setSound(defaultSound)
                                            .setContentIntent(pendingIntent);

                                    NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




                                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                                    assert noti != null;
                                    noti.notify(m,builder.build());


                                }*/

                            }

                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });

                //Toast.makeText(HomeActivity.this, memory_models.size()+"", Toast.LENGTH_LONG).show();








                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 6));



            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }



    @Override
    protected void onStop () {
        super .onStop() ;

        //NotificationService notificationService = new NotificationService(firebaseUser.getUid(), latLngg);

        startService( new Intent( this, NotificationService. class )) ;

    }



    public void closeApp (View view) {
        finish() ;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }






    private void read_memory() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Memories");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Memory_Model memory_model = snapshot.getValue(Memory_Model.class);


                    if (memory_model != null && memory_model.getUserid().equals(firebaseUser.getUid())) {

                        memory_models.add(memory_model);

                    }

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



    }






    private void initView() {

        showbtn = (FloatingActionButton) findViewById(R.id.showbtn);
        addbtn = (FloatingActionButton) findViewById(R.id.addbtn);
    }
}











