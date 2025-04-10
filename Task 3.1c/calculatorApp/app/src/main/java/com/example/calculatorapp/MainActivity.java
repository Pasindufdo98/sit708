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
