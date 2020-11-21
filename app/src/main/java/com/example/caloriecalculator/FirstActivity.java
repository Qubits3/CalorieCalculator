package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setTitle("Giriş Sayfası");

    }
    public void onaylaButonu(View view){
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);
    }
}