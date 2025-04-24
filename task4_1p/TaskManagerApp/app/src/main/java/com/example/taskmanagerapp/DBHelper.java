package com.example.taskmanagerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "TaskManager.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE Tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, due_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        DB.execSQL("DROP TABLE IF EXISTS Tasks");
        onCreate(DB);
    }

    // Insert a new task
    public Boolean insertTask(String title, String description, String dueDate) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("due_date", dueDate);

        long result = DB.insert("Tasks", null, contentValues);
        return result != -1;
    }

    // Update a task
    public Boolean updateTask(int id, String title, String description, String dueDate) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("due_date", dueDate);

        long result = DB.update("Tasks", contentValues, "id=?", new String[]{String.valueOf(id)});
        return result != -1;
    }

    // Delete a task
    public Boolean deleteTask(int id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("Tasks", "id=?", new String[]{String.valueOf(id)});
        return result != -1;
    }
    // Get all tasks
    public Cursor getAllTasks() {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.rawQuery("SELECT * FROM Tasks ORDER BY due_date ASC", null);
    }
}
