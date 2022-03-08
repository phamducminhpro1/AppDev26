package com.example.testmapproject;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.testmapproject.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);
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

//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//
//        // Add a marker in Sydney and move the camera
//        LatLng eindhoven = new LatLng(51.441642, 5.4697225);
//        LatLng Tue = new LatLng(51.448091, 5.490042);
//        LatLng job1 = new LatLng(51.416445, 5.453188);
//        LatLng job2= new LatLng(51.446593, 5.525943);
//        LatLng job3 = new LatLng(51.419271, 5.508225);
//        mMap.addMarker(new MarkerOptions().position(Tue).title("Marker in Eindhoven"));
//        mMap.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                        Tue,
//                        9f
//                )
//        );
//        mMap.addPolyline(new PolylineOptions()
//                .add(Tue)
//                .add(job1)
//                .add(job2)
//                .add(job3)
//                .width(2f)
//                .color(Color.RED)
//        );
//        mMap.addCircle(new CircleOptions()
//                .center(Tue)
//
//                .radius(500.0)
//                .strokeWidth(3f)
//                .strokeColor(Color.RED)
//                .fillColor(Color.argb(70, 150, 50, 50))
//        );

        try {
            List<Address> addresses = geocoder.getFromLocationName("Tilburg",1);
            Address address = addresses.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(address.getLocality());

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}