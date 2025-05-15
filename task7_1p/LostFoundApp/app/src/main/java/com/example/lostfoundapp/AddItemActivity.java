package com.example.lostfoundapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private RadioGroup radioGroupType;
    private Button buttonSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dbHelper = new DatabaseHelper(this);

        radioGroupType = findViewById(R.id.radioGroupType);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
                String name = editTextName.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();
                String location = editTextLocation.getText().toString().trim();

                if (selectedTypeId == -1) {
                    Toast.makeText(AddItemActivity.this, "Please select Lost or Found", Toast.LENGTH_SHORT).show();
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

                saveToDatabase(type, name, phone, description, date, location);
            }
        });
    }

    private void saveToDatabase(String type, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TYPE, type);
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);

        long newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Advert Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show();
        }
    }
}
