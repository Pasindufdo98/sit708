package com.example.unitconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner sourceUnit, targetUnit;
    Button convertButton;
    TextView resultText;

    String[] units = {"Meters", "Kilometers", "Grams", "Kilograms", "Celsius", "Fahrenheit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        sourceUnit = findViewById(R.id.sourceUnit);
        targetUnit = findViewById(R.id.targetUnit);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sourceUnit.setAdapter(adapter);
        targetUnit.setAdapter(adapter);

        // button
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputStr = inputValue.getText().toString();
                if (inputStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                    return;
                }

                double input = Double.parseDouble(inputStr);
                String fromUnit = sourceUnit.getSelectedItem().toString();
                String toUnit = targetUnit.getSelectedItem().toString();
                double result = convertUnits(input, fromUnit, toUnit);
                resultText.setText(String.format("%.2f %s", result, toUnit));
            }
        });
    }

    private double convertUnits(double value, String from, String to) {
        if (from.equals(to)) return value;

        // Length
        if (from.equals("Meters") && to.equals("Kilometers")) return value / 1000;
        if (from.equals("Kilometers") && to.equals("Meters")) return value * 1000;

        // Weight
        if (from.equals("Grams") && to.equals("Kilograms")) return value / 1000;
        if (from.equals("Kilograms") && to.equals("Grams")) return value * 1000;

        // Temperature
        if (from.equals("Celsius") && to.equals("Fahrenheit")) return (value * 9/5) + 32;
        if (from.equals("Fahrenheit") && to.equals("Celsius")) return (value - 32) * 5/9;

        // unsupported
        Toast.makeText(this, "Conversion not supported", Toast.LENGTH_SHORT).show();
        return 0;
    }
}
