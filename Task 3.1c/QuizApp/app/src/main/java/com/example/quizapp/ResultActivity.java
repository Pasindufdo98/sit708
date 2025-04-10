package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreText = findViewById(R.id.scoreText);
        Button retryButton = findViewById(R.id.retryButton);
        Button finishButton = findViewById(R.id.finishButton);

        // Get score data from intent
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);

        // Display score
        scoreText.setText("Your Score: " + score + "/" + total);

        // Retry the quiz
        retryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Finish the app
        finishButton.setOnClickListener(v -> finishAffinity());
    }
}
