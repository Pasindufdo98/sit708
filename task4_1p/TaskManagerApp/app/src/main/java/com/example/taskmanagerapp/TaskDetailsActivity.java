package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView textTitle, textDescription, textDueDate;
    Button buttonEdit, buttonDelete;
    DBHelper dbHelper;
    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textDueDate = findViewById(R.id.textDueDate);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        taskId = intent.getIntExtra("TASK_ID", -1);
        String title = intent.getStringExtra("TASK_TITLE");
        String description = intent.getStringExtra("TASK_DESCRIPTION");
        String dueDate = intent.getStringExtra("TASK_DUEDATE");

        textTitle.setText(title);
        textDescription.setText(description);
        textDueDate.setText("Due: " + dueDate);

        buttonEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(TaskDetailsActivity.this, AddEditTaskActivity.class);
            editIntent.putExtra("TASK_ID", taskId);
            editIntent.putExtra("TASK_TITLE", title);
            editIntent.putExtra("TASK_DESCRIPTION", description);
            editIntent.putExtra("TASK_DUEDATE", dueDate);
            startActivity(editIntent);
        });

        buttonDelete.setOnClickListener(v -> {
            boolean deleted = dbHelper.deleteTask(taskId);
            if (deleted) {
                Toast.makeText(TaskDetailsActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(TaskDetailsActivity.this, "Failed to Delete Task", Toast.LENGTH_SHORT).show();
            }
        });

        // Back Button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Task Details");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                Intent homeIntent = new Intent(TaskDetailsActivity.this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
