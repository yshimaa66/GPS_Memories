package com.example.gps_memories.controller.Memory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_memories.R;
import com.example.gps_memories.adapter.Edit_Memory_Photos_Adapter;
import com.example.gps_memories.adapter.View_Photo.View_Photos_Adapter;
import com.example.gps_memories.model.Memory_Model;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class View_MemoryActivity extends AppCompatActivity {

    protected TextView timeTextView;
    protected RecyclerView memoryPhotosRC;
    protected TextView titleTextView;
    protected TextView addressTextView;
    protected ImageView map;
    protected TextView descriptionTextView;

    private double latitude;

    private double longitude;

    View_Photos_Adapter view_photos_adapter;

    String memory_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_view__memory);
        initView();

        Intent intent = getIntent();

        memory_id = intent.getStringExtra("memory_id");

        read_memory(memory_id);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(View_MemoryActivity.this, MapActivity.class);


                intent.putExtra("latitudestr", latitude);

                intent.putExtra("longitudestr", longitude);


                startActivity(intent);

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);


            }
        });

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

                        titleTextView.setText(memory_model.getTitle());

                        descriptionTextView.setText(memory_model.getDescription());


                        memoryPhotosRC.setHasFixedSize(true);
                        memoryPhotosRC.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


                        view_photos_adapter = new View_Photos_Adapter(memory_model.getPhotos(), getApplicationContext());

                        memoryPhotosRC.setAdapter(view_photos_adapter);


                    }
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



    }


    private void initView() {
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        memoryPhotosRC = (RecyclerView) findViewById(R.id.memory_photosRC);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        map = (ImageView) findViewById(R.id.map);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
    }
}
