package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class ThirdActivity extends AppCompatActivity {
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        editText = findViewById(R.id.editText);
    }

    @Override
    public void onBackPressed() {
        Singleton.getInstance().message = editText.getText().toString();

        super.onBackPressed();
    }
}