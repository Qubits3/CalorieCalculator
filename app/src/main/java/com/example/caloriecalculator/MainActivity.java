package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void walkActivity(View view){
        Intent walk = new Intent(getApplicationContext(), WalkActivity.class);
        startActivity(walk);
    }
    public void bicycleActivity(View view){
        Intent bicycle = new Intent(getApplicationContext(), BicycleActivity.class);
        startActivity(bicycle);
    }
    public void graphActivity(View view){
        Intent graph = new Intent(getApplicationContext(), GraphActivity.class);
        startActivity(graph);
    }
}