package com.example.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class PlacePickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng = null;
    private String selectedAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        if (!Places.isInitialized()) {

            Places.initialize(getApplicationContext(), "AIzaSyD4a9lXhNy-OONEgMlh_p951REGU9SY8-s");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.place_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Autocomplete Search Bar
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
            ));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedLatLng = place.getLatLng();
                    selectedAddress = place.getAddress();

                    if (selectedLatLng != null && mMap != null) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .position(selectedLatLng)
                                .title(place.getName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));


                        Intent result = new Intent();
                        result.putExtra("selected_address", selectedAddress);
                        result.putExtra("lat", selectedLatLng.latitude);
                        result.putExtra("lng", selectedLatLng.longitude);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(PlacePickerActivity.this,
                            "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Default location: Melbourne
        LatLng melbourne = new LatLng(-37.8136, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 12));
    }
}
