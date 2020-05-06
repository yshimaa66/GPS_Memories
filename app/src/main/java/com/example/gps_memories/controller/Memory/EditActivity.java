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
import com.example.gps_memories.adapter.Edit_Memory_Photos_Adapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EditActivity extends AppCompatActivity {

    protected EditText titleEditText;
    protected TextView addressTextView;
    protected ImageView map;
    protected TextView timeTextView;
    protected EditText descriptionEditText;
    protected RecyclerView memoryPhotosRC;
    protected ImageView memoryPhotobtn;
    protected FloatingActionButton saveMemorybtn;


    Edit_Memory_Photos_Adapter edit_memory_photos_adapter;

    private double latitude;

    private double longitude;

    private StorageReference storageReference;
    private FirebaseStorage storage;


    public static List<String> editmemoryphotoslist;

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
        super.setContentView(R.layout.activity_edit);
        initView();



        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();

        memory_id = intent.getStringExtra("memory_id");

        read_memory(memory_id);


        editmemoryphotoslist = new ArrayList<>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(EditActivity.this);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        memoryPhotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OpenImage();

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditActivity.this, MapActivity.class);


                intent.putExtra("latitudestr", latitude);

                intent.putExtra("longitudestr", longitude);


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

                } else if (editmemoryphotoslist.size() == 0) {

                    Toast.makeText(EditActivity.this,
                            "Your memory needs to have photo", Toast.LENGTH_SHORT).show();


                } else {

                    Memory_Model memoryModel = new Memory_Model(memory_id,firebaseUser.getUid(),titleEditText.getText().toString(),
                            descriptionEditText.getText().toString(),timeTextView.getText().toString(),addressTextView.getText().toString()
                            ,latitude,longitude,editmemoryphotoslist);

                    FirebaseDatabase.getInstance().getReference("Memories")
                            .child(memory_id)
                            .setValue(memoryModel);


                    Toast.makeText(EditActivity.this, "Memory has been edited successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditActivity.this, HomeActivity.class);


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

        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
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

        final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
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


                        editmemoryphotoslist.add(auri);


                        progressDialog.dismiss();


                        storageReference = FirebaseStorage.getInstance().getReference("uploads");


                        memoryPhotosRC.setHasFixedSize(true);
                        memoryPhotosRC.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                        // Toast.makeText(Create_Coworking_SpaceActivity.this, photoslist.get(0), Toast.LENGTH_SHORT).show();


                        edit_memory_photos_adapter = new Edit_Memory_Photos_Adapter(editmemoryphotoslist, getApplicationContext());

                        memoryPhotosRC.setAdapter(edit_memory_photos_adapter);


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



    private void read_memory(final String memory_id) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Memories");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                   Memory_Model memory_model = snapshot.getValue(Memory_Model.class);


                    if (memory_model.getId().equals(memory_id)) {

                        latitude=memory_model.getLatitude();
                        longitude=memory_model.getLongitude();
                        addressTextView.setText(memory_model.getAddress());
                        timeTextView.setText(memory_model.getTime());

                        titleEditText.setText(memory_model.getTitle());

                        descriptionEditText.setText(memory_model.getDescription());


                        memoryPhotosRC.setHasFixedSize(true);
                        memoryPhotosRC.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


                        edit_memory_photos_adapter = new Edit_Memory_Photos_Adapter(memory_model.getPhotos(), getApplicationContext());

                        memoryPhotosRC.setAdapter(edit_memory_photos_adapter);

                        editmemoryphotoslist = memory_model.getPhotos();

                    }
                    }




                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



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
