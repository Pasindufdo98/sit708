package com.example.lostfoundapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewItemActivity extends AppCompatActivity {

    private TextView textViewName, textViewDate, textViewLocation;
    private Button buttonRemove;
    private int itemId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        dbHelper = new DatabaseHelper(this);

        textViewName = findViewById(R.id.textViewName);
        textViewDate = findViewById(R.id.textViewDate);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        textViewLocation = findViewById(R.id.textViewLocation);
        buttonRemove = findViewById(R.id.buttonRemove);

        Intent intent = getIntent();
        if (intent != null) {
            itemId = intent.getIntExtra("item_id", -1);
            String name = intent.getStringExtra("item_name");
            String description = intent.getStringExtra("item_description");
            String phone = intent.getStringExtra("item_phone");
            String date = intent.getStringExtra("item_date");
            String location = intent.getStringExtra("item_location");

            textViewName.setText(name);
            textViewDate.setText(date);
            textViewDescription.setText(description);
            textViewPhone.setText(phone);
            textViewLocation.setText(location);
        }

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });
    }

    private void deleteItem() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});
        Toast.makeText(this, "Item Removed", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
