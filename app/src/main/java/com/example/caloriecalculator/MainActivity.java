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

    public void firstActivity(View view){
        Intent first = new Intent(getApplicationContext(), FirstActivity.class);
        startActivity(first);
    }
    public void walkActivity(View view){
        Intent walk = new Intent(getApplicationContext(), MapsActivity.class);
        walk.putExtra("profile", "walking");
        startActivity(walk);
    }
    public void bicycleActivity(View view){
        Intent bicycle = new Intent(getApplicationContext(), MapsActivity.class);
        bicycle.putExtra("profile", "cycling");
        startActivity(bicycle);
    }
    public void graphActivity(View view){
        Intent graph = new Intent(getApplicationContext(), GraphActivity.class);
        startActivity(graph);
    }

    @Override
    public void onBackPressed() {
        // Disable back button
    }
}