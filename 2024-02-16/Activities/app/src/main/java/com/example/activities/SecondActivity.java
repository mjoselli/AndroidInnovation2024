package com.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    Button buttonGoToThird;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        buttonGoToThird = findViewById(R.id.buttonGoToThird);
        textView = findViewById(R.id.textView);
        buttonGoToThird.setOnClickListener(view -> {
            Intent intent = new Intent(SecondActivity.this,
                    ThirdActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textView.setText(Singleton.getInstance().message);
    }
}