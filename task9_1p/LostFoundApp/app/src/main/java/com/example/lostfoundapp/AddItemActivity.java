package com.example.lostfoundapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private RadioGroup radioGroupType;
    private Button buttonSave, buttonGetLocation;

    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int PLACE_PICKER_REQUEST_CODE = 200;

    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dbHelper = new DatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        radioGroupType = findViewById(R.id.radioGroupType);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);
        buttonGetLocation = findViewById(R.id.buttonGetLocation);

        editTextLocation.setFocusable(false);
        editTextLocation.setClickable(true);
        editTextLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddItemActivity.this, PlacePickerActivity.class);
            startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE);
        });

        buttonGetLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                fetchCurrentLocation();
            }
        });

        buttonSave.setOnClickListener(v -> saveAdvert());
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        editTextLocation.setText(latitude + ", " + longitude);
                        Log.d("DEBUG_LOC", "Lat: " + latitude + ", Lng: " + longitude);
                    } else {
                        Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG_LOC", "Location error", e);
                });
    }

    private void saveAdvert() {
        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required");
            return;
        }
        if (description.isEmpty()) {
            editTextDescription.setError("Description is required");
            return;
        }
        if (date.isEmpty()) {
            editTextDate.setError("Date is required");
            return;
        }
        if (location.isEmpty()) {
            editTextLocation.setError("Location is required");
            return;
        }

        RadioButton selectedRadio = findViewById(selectedTypeId);
        String type = selectedRadio.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TYPE, type);
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        values.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        values.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);

        Log.d("DEBUG_LATLNG", "Lat: " + latitude + ", Lng: " + longitude);

        long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Advert Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("selected_address");
            double lat = data.getDoubleExtra("lat", 0.0);
            double lng = data.getDoubleExtra("lng", 0.0);

            editTextLocation.setText(address);
            latitude = lat;
            longitude = lng;
        }
    }
}
