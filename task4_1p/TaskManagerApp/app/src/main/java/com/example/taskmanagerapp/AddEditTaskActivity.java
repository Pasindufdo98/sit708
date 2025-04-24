package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {

    EditText editTextTitle, editTextDescription, editTextDueDate;
    Button buttonSaveTask;
    DBHelper dbHelper;
    int taskId = -1; // Default: -1 = new task

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDueDate = findViewById(R.id.editTextDueDate);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);

        dbHelper = new DBHelper(this);


        editTextDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditTaskActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                            editTextDueDate.setText(formattedDate);
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // Load task for edit
        Intent intent = getIntent();
        if (intent.hasExtra("TASK_ID")) {
            taskId = intent.getIntExtra("TASK_ID", -1);
            editTextTitle.setText(intent.getStringExtra("TASK_TITLE"));
            editTextDescription.setText(intent.getStringExtra("TASK_DESCRIPTION"));
            editTextDueDate.setText(intent.getStringExtra("TASK_DUEDATE"));
        }

        // Save Task
        buttonSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String dueDate = editTextDueDate.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(AddEditTaskActivity.this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dueDate.isEmpty()) {
                    Toast.makeText(AddEditTaskActivity.this, "Due date cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    Toast.makeText(AddEditTaskActivity.this, "Due date must be in YYYY-MM-DD format", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success;
                if (taskId == -1) {
                    success = dbHelper.insertTask(title, description, dueDate);
                } else {
                    success = dbHelper.updateTask(taskId, title, description, dueDate);
                }

                if (success) {
                    Toast.makeText(AddEditTaskActivity.this, "Task saved", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditTaskActivity.this, "Error saving task", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Delete Task
        Button buttonDeleteTask = findViewById(R.id.buttonDeleteTask);
        if (taskId != -1) {
            buttonDeleteTask.setVisibility(View.VISIBLE);
        }

        buttonDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskId != -1) {
                    boolean deleted = dbHelper.deleteTask(taskId);
                    if (deleted) {
                        Toast.makeText(AddEditTaskActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddEditTaskActivity.this, "Failed to Delete Task", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
