package com.example.firstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editTextNumber;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editTextNumber = findViewById(R.id.editTextNumber);
        button = findViewById(R.id.button);
        textView.setText("Hello World");
        editTextNumber.setText("0");
        button.setText("Add One");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = editTextNumber.getText().toString();
                int number = Integer.valueOf(value);
                number++;
                editTextNumber.setText(String.valueOf(number));
            }
        });
    }
}