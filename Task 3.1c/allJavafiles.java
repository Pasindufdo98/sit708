// Quiz app 
// MainActivity

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


// Quiz Activity

package com.example.quizapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView welcomeText, questionCounterText, questionTitle, questionText;
    private Button answerButton1, answerButton2, answerButton3, submitNextButton;
    private ProgressBar progressBar;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int selectedAnswerIndex = -1;
    private int score = 0;
    private boolean submitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        welcomeText = findViewById(R.id.welcomeText);
        questionCounterText = findViewById(R.id.questionCounterText);
        questionTitle = findViewById(R.id.questionTitle);
        questionText = findViewById(R.id.questionText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        submitNextButton = findViewById(R.id.submitNextButton);
        progressBar = findViewById(R.id.progressBar);

        String userName = getIntent().getStringExtra("userName");
        welcomeText.setText("Welcome " + userName + "!");

        questions = new ArrayList<>();
        questions.add(new Question("Networking", "What does HTTP stand for?",
                Arrays.asList("HyperText Transfer Protocol", "Hyperlink Text Transition Protocol", "High-Tech Transfer Package"), 0));
        questions.add(new Question("Hardware", "Which of the following is a non-volatile memory?",
                Arrays.asList("RAM", "Cache", "ROM"), 2));
        questions.add(new Question("Web & Networking", "What is the main purpose of a DNS server?",
                Arrays.asList("Store files in the cloud", "Convert domain names into IP addresses", "Block unauthorized access"), 1));
        questions.add(new Question("Operating Systems", "Which of the following is an operating system?",
                Arrays.asList("HTML", "Linux", "SQL"), 1));
        questions.add(new Question("Cybersecurity", "What is phishing?",
                Arrays.asList("Sending spam emails for marketing", "Tricking people into giving personal information", "Installing updates on a computer"), 1));

        answerButton1.setOnClickListener(view -> selectAnswer(0));
        answerButton2.setOnClickListener(view -> selectAnswer(1));
        answerButton3.setOnClickListener(view -> selectAnswer(2));
        submitNextButton.setOnClickListener(view -> {
            if (!submitted) submitAnswer();
            else goToNextQuestion();
        });

        showQuestion();
    }

    private void showQuestion() {
        submitted = false;
        selectedAnswerIndex = -1;

        Question q = questions.get(currentQuestionIndex);
        questionCounterText.setText((currentQuestionIndex + 1) + "/" + questions.size());
        questionTitle.setText(q.getTitle());
        questionText.setText(q.getText());
        answerButton1.setText(q.getOptions().get(0));
        answerButton2.setText(q.getOptions().get(1));
        answerButton3.setText(q.getOptions().get(2));

        resetAnswerButtonColors();
        submitNextButton.setText("Submit");

        int progress = (int) (((float) currentQuestionIndex / questions.size()) * 100);
        progressBar.setProgress(progress);
    }

    private void selectAnswer(int index) {
        selectedAnswerIndex = index;
        resetAnswerButtonColors();

        if (index == 0) answerButton1.setBackgroundColor(Color.LTGRAY);
        else if (index == 1) answerButton2.setBackgroundColor(Color.LTGRAY);
        else answerButton3.setBackgroundColor(Color.LTGRAY);
    }

    private void submitAnswer() {
        if (selectedAnswerIndex == -1) return;

        Question q = questions.get(currentQuestionIndex);
        submitted = true;

        Button[] buttons = {answerButton1, answerButton2, answerButton3};
        for (int i = 0; i < buttons.length; i++) {
            if (i == q.getCorrectAnswerIndex()) buttons[i].setBackgroundColor(Color.GREEN);
            else if (i == selectedAnswerIndex) buttons[i].setBackgroundColor(Color.RED);
        }

        if (selectedAnswerIndex == q.getCorrectAnswerIndex()) score++;
        submitNextButton.setText(currentQuestionIndex == questions.size() - 1 ? "Finish" : "Next");
    }

    private void goToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) showQuestion();
        else {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("total", questions.size());
            startActivity(intent);
            finish();
        }
    }

    private void resetAnswerButtonColors() {
        answerButton1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        answerButton2.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        answerButton3.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    }
}

// Quesition

package com.example.quizapp;

import java.util.List;

public class Question {
    private String title;
    private String text;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String title, String text, List<String> options, int correctAnswerIndex) {
        this.title = title;
        this.text = text;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getTitle() { return title; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
}

// ResultActivity

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

/////////////////////////////////// Simple Calculator App 

// MainActivity

package com.example.calculatorapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText input1, input2;
    Button addButton, subtractButton;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Link UI elements
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        addButton = findViewById(R.id.addButton);
        subtractButton = findViewById(R.id.subtractButton);
        resultText = findViewById(R.id.resultText);

        // Set click listeners
        addButton.setOnClickListener(v -> calculate('+'));
        subtractButton.setOnClickListener(v -> calculate('-'));
    }

    private void calculate(char operation) {
        String val1 = input1.getText().toString().trim();
        String val2 = input2.getText().toString().trim();

        if (TextUtils.isEmpty(val1) || TextUtils.isEmpty(val2)) {
            Toast.makeText(this, "Please enter both numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double num1 = Double.parseDouble(val1);
            double num2 = Double.parseDouble(val2);
            double result = (operation == '+') ? num1 + num2 : num1 - num2;
            resultText.setText("Result: " + result);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }
}
