package com.example.gps_memories.controller.Memory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.gps_memories.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    private double latitudestr;

    private double longitudestr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        Intent intent = getIntent();

        latitudestr=intent.getDoubleExtra("latitudestr",latitudestr);

        longitudestr=intent.getDoubleExtra("longitudestr",longitudestr);

        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentlocation = new LatLng(latitudestr, longitudestr);
        mMap.addMarker(new MarkerOptions().position(currentlocation).title("Here's your co-working space"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlocation));

        float zoomLevel = 11.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, zoomLevel));


    }


}
