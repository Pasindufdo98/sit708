package com.example.lostfoundapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListItemsActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private ArrayList<String> itemsList;
    private ArrayList<Integer> itemIds;
    private ArrayList<String> itemDates;
    private ArrayList<String> itemLocations;
    private ArrayList<String> itemDescriptions;
    private ArrayList<String> itemPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_items);

        listView = findViewById(R.id.listViewItems);
        dbHelper = new DatabaseHelper(this);
        itemsList = new ArrayList<>();
        itemIds = new ArrayList<>();
        itemDates = new ArrayList<>();
        itemLocations = new ArrayList<>();
        itemDescriptions = new ArrayList<>();
        itemPhones = new ArrayList<>();

        loadItems();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openViewItemActivity(position);
            }
        });
    }

    private void loadItems() {
        itemsList.clear();
        itemIds.clear();
        itemDates.clear();
        itemLocations.clear();
        itemDescriptions.clear();
        itemPhones.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION));

            itemsList.add(type + ": " + name);
            itemIds.add(id);
            itemDates.add(date);
            itemLocations.add(location);
            itemDescriptions.add(description);
            itemPhones.add(phone);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, itemsList);
        listView.setAdapter(adapter);
    }

    private void openViewItemActivity(int position) {
        Intent intent = new Intent(ListItemsActivity.this, ViewItemActivity.class);
        intent.putExtra("item_id", itemIds.get(position));
        intent.putExtra("item_name", itemsList.get(position));
        intent.putExtra("item_description", itemDescriptions.get(position));
        intent.putExtra("item_phone", itemPhones.get(position));
        intent.putExtra("item_date", itemDates.get(position));
        intent.putExtra("item_location", itemLocations.get(position));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadItems();
        }
    }
}
