
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
