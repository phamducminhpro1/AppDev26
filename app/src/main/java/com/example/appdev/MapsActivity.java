package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.appdev.databinding.ActivityMapsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;
    private Marker myMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize geocoder
        geocoder = new Geocoder(this);
    }

    public void MapsAddresses() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jobs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Job job = s.getValue(Job.class);

                    //Concatenate address + city
                    String addresscity = job.street + " " + job.city;

                    //Add addresses
                    AddressToLngLat(addresscity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        AddressToLngLat("Amsterdam");
//        AddressToLngLat("London");
        MapsAddresses();
        EindhovenMarker();

        //Zoom buttons in right bottom corner
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //If rotating, compass is shown
        mMap.getUiSettings().setCompassEnabled(true);

        // Listener for click on marker
        mMap.setOnMarkerClickListener(this);
    }

    //Adds marker on map at Eindhoven University of Technology
    public void EindhovenMarker() {
        LatLng tueLoc = new LatLng(51.448024, 5.490468);
        mMap.addMarker(new MarkerOptions().position(tueLoc).title("Marker in Tue"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tueLoc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                tueLoc,
                14f
                )
        );
    }

    //Adds marker on map with address + city as input string
    public void AddressToLngLat(String location) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            Address address = addresses.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //Add marker of address
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(address.getLocality());
            mMap.addMarker(markerOptions);

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        if (marker.equals(myMarker))
        {
            String markerId = marker.getId();
            String jobTitle = marker.getTitle();
        }

        return false;
    }

}