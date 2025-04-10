
package com.example.quizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText editTextName;
    Button startButton;
    public static final String USER_PREFS = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.screen_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextName = findViewById(R.id.editTextName);
        startButton = findViewById(R.id.startButton);

        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        String savedName = prefs.getString("userName", "");
        editTextName.setText(savedName);

        startButton.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", name);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("userName", name);
            startActivity(intent);
        });
    }
}
