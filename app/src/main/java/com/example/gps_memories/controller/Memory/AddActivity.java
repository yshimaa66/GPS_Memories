package com.example.gps_memories.controller.Memory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_memories.R;
import com.example.gps_memories.adapter.Memory_Photos_Adapter;
import com.example.gps_memories.controller.HomeActivity;
import com.example.gps_memories.model.Memory_Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddActivity extends AppCompatActivity {

    protected EditText titleEditText;
    protected TextView addressTextView;
    protected ImageView map;
    protected TextView timeTextView;
    protected EditText descriptionEditText;
    protected RecyclerView memoryPhotosRC;
    protected ImageView memoryPhotobtn;
    protected FloatingActionButton saveMemorybtn;

    Memory_Photos_Adapter memory_photos_adapter;

    private double latitudestr;

    private double longitudestr;

    private StorageReference storageReference;
    private FirebaseStorage storage;


    public static List<String> memoryphotoslist;

    private static final int image_request = 1;
    private Uri imageuri;
    private StorageTask<UploadTask.TaskSnapshot> uploadtask;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    FirebaseUser firebaseUser;

    String memory_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add);
        initView();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        long time = System.currentTimeMillis();

        Random r = new Random();

        String word = firebaseUser.getUid() + "Spacing" + time;

        memory_id = scramble(r, word);


        memoryphotoslist = new ArrayList<>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(AddActivity.this);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        String timestr = new SimpleDateFormat("dd/MM/yyyy  h:mm a", Locale.getDefault()).format(new Date());


        timeTextView.setText(timestr);


        memoryPhotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OpenImage();

            }
        });



        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, MapActivity.class);


                intent.putExtra("latitudestr", latitudestr);

                intent.putExtra("longitudestr", longitudestr);


                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });






        saveMemorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (titleEditText.getText().toString().trim().equals("")) {

                    titleEditText.setError("required");
                }

                else if (descriptionEditText.getText().toString().trim().equals("")) {

                    descriptionEditText.setError("required");

                } else if (memoryphotoslist.size() == 0) {

                    Toast.makeText(AddActivity.this,
                            "Your memory needs to have photo", Toast.LENGTH_SHORT).show();


                } else {

                   Memory_Model memoryModel = new Memory_Model(memory_id,firebaseUser.getUid(),titleEditText.getText().toString(),
                           descriptionEditText.getText().toString(),timeTextView.getText().toString(),addressTextView.getText().toString()
                           ,latitudestr,longitudestr,memoryphotoslist);

                    FirebaseDatabase.getInstance().getReference("Memories")
                            .child(memory_id)
                            .setValue(memoryModel);


                    Toast.makeText(AddActivity.this, "Memory has been created successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddActivity.this, HomeActivity.class);


                    startActivity(intent);

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                    finish();

                }






            }
        });








    }


    public static String scramble(Random random, String inputString) {
        // Convert your string into a simple char array:
        char a[] = inputString.toCharArray();

        // Scramble the letters using the standard Fisher-Yates shuffle,
        for (int i = 0; i < a.length; i++) {
            int j = random.nextInt(a.length);
            // Swap letters
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }

        return new String(a);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                AddActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {


                                    Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {


                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                        latitudestr = location.getLatitude();

                                        longitudestr = location.getLongitude();

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    String cityName = addresses.get(0).getAddressLine(0);
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);


                                    addressTextView.setText(cityName);


                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Geocoder geocoder = new Geocoder(AddActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

                latitudestr = mLastLocation.getLatitude();

                longitudestr = mLastLocation.getLongitude();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getAddressLine(0);
            String stateName = addresses.get(0).getAddressLine(1);
            String countryName = addresses.get(0).getAddressLine(2);

            addressTextView.setText(cityName);

        }


    };




    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        IsFinish(" The changes you made won't be saved ");
    }


    public void IsFinish(String alertmessage) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Process.killProcess(Process.myPid());
                        // This above line close correctly
                        //finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setMessage(alertmessage)
                .setPositiveButton("Ok", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();

    }


    private void OpenImage() {


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, image_request);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


    }

    private String getFileExtension(Uri uri) {


        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));


    }


    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(AddActivity.this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();


        if (imageuri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageuri));


            uploadtask = fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {


                    if (!task.isSuccessful()) {

                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();


                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {


                        Uri downloadUri = (Uri) task.getResult();

                        String auri = downloadUri.toString();

/*
                        DatabaseReference referenceee= FirebaseDatabase.getInstance()
                                .getReference("Coworking_Spaces").child(Coworking_Spaceid);

                        referenceee.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                                photoslist.clear();

                                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                    Coworking_Space_Model coworking_space_model = snapshot.getValue(Coworking_Space_Model.class);



                                    photoslist.addAll(coworking_space_model.getPhotos().toString());





                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





*/

                        memoryphotoslist.add(auri);


                        progressDialog.dismiss();


                        storageReference = FirebaseStorage.getInstance().getReference("uploads");


                        memoryPhotosRC.setHasFixedSize(true);
                        memoryPhotosRC.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                        // Toast.makeText(Create_Coworking_SpaceActivity.this, photoslist.get(0), Toast.LENGTH_SHORT).show();


                        memory_photos_adapter = new Memory_Photos_Adapter(memoryphotoslist, getApplicationContext());

                        memoryPhotosRC.setAdapter(memory_photos_adapter);


                    } else {

                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();


                }
            });


        } else {


            Toast.makeText(getApplicationContext(), "No image selected", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == image_request && resultCode == RESULT_OK

                && data != null && data.getData() != null
        ) {

            imageuri = data.getData();

            if (uploadtask != null && uploadtask.isInProgress()) {


                Toast.makeText(getApplicationContext(), "Upload in progress", Toast.LENGTH_SHORT).show();


            } else {

                uploadImage();

            }


        }
    }

    private void initView() {
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        map = (ImageView) findViewById(R.id.map);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        memoryPhotosRC = (RecyclerView) findViewById(R.id.memory_photosRC);
        memoryPhotobtn = (ImageView) findViewById(R.id.memory_photobtn);
        saveMemorybtn = (FloatingActionButton) findViewById(R.id.save_memorybtn);
    }
}
