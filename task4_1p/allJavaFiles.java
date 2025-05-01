Task 4.P
GitHub Repo Link:  https://github.com/Pasindufdo98/sit708/tree/main/task4_1p/TaskManagerApp
Source code 

MainActivity.java

package com.example.taskmanagerapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fabAddTask;
    DBHelper dbHelper;
    ArrayList<Task> taskList;
    TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fabAddTask);
        dbHelper = new DBHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTasks();

        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                return true;
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        taskList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllTasks();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Tasks Found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            String dueDate = cursor.getString(3);
            taskList.add(new Task(id, title, description, dueDate));
        }

        taskAdapter = new TaskAdapter(this, taskList);
        recyclerView.setAdapter(taskAdapter);
    }
}





DBHelper.java
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



AddEditTaskActivity.java

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



Task.java

package com.example.taskmanagerapp;

public class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;

    public Task(int id, String title, String description, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}






TaskAdapter.java

package com.example.taskmanagerapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    Context context;
    ArrayList<Task> taskList;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.dueDate.setText("Due: " + task.getDueDate());

        // Click on a task to edit it
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TaskDetailsActivity.class);
            intent.putExtra("TASK_ID", task.getId());
            intent.putExtra("TASK_TITLE", task.getTitle());
            intent.putExtra("TASK_DESCRIPTION", task.getDescription());
            intent.putExtra("TASK_DUEDATE", task.getDueDate());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, dueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.taskTitle);
            dueDate = itemView.findViewById(R.id.taskDueDate);
        }
    }
}




TaskDetailsActvity.java
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

