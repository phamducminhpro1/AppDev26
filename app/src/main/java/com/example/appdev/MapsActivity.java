package com.example.appdev;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Map;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;
    private Marker selfMarker;

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

    //Reads addresses from database and creates strings based on street and city
    //Runs AddressToLngLat with created string and the corresponding job id
    //No input needed
    public void MapsAddresses() {
        //Connect to firebase database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Jobs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Job job = s.getValue(Job.class);

                    //Concatenate address + city
                    String addressCity = job.street + " " + job.city;

                    //JobId
                    String jobId = job.id;

                    //Add addresses
                    AddressToLngLat(addressCity, jobId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            addSelfMarker();
        } else {
            return;
        }
    });


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

        MapsAddresses();
        EindhovenMarker();
        addSelfMarker();

        //Zoom buttons in right bottom corner
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //If rotating, compass is shown
        mMap.getUiSettings().setCompassEnabled(true);

        // Listener for click on marker
        mMap.setOnMarkerClickListener(this);
    }

    //Adds marker to current location of device
    private void addSelfMarker() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // If we don't have permission to the location yet, we should ask for it.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );

            return;
        }

        // Make this class the listener for updating the user position:
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
    }

    //Adds marker on map at Eindhoven University of Technology
    private void EindhovenMarker() {
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
    private void AddressToLngLat(String location, String jobId) {
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses.isEmpty()) {
                return;
            }

            Address address = addresses.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            //Add marker of address
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(address.getLocality());

            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(jobId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //On click of marker on map, the corresponding job description page will open
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag() == null) {
            return false;
        }

        String jobId = (String)marker.getTag();

        Intent intent = new Intent(MapsActivity.this, JobDescriptionActivity.class);
        intent.putExtra("jobId", jobId);
        startActivity(intent);
        return true;
    }

    //Update current location marker of user when moving
    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng selfPos = new LatLng(location.getLatitude(), location.getLongitude());
        if (selfMarker == null) {
            selfMarker = mMap.addMarker(new MarkerOptions()
                    .title("Your location")
                    .position(selfPos)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );

            mMap.moveCamera(CameraUpdateFactory.newLatLng(selfPos));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    selfPos,
                    14f
                    )
            );
        }

        selfMarker.setPosition(selfPos);
    }
}