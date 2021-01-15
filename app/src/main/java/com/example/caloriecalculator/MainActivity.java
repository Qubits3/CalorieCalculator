package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedP;
    String savedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedP = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);

//        String savedName = sharedP.getString("isim", "İsim bulunamadı");
        savedGender = sharedP.getString("cinskey", "Belirsiz");

        if(savedGender.equals("Belirsiz")){
            Intent first = new Intent(getApplicationContext(), FirstActivity.class);
            startActivity(first);
        }

        updateCalorieText();
    }

    protected void onResume(){
        super.onResume();

        updateCalorieText();
    }

    private void updateCalorieText(){
        String kalori = sharedP.getString("dailyCalorieOfAllDay", "0.0");

        TextView yazi = findViewById(R.id.kaloriVew);

        kalori = kalori.substring(0,kalori.indexOf("."));
        yazi.setText("Bugün " + kalori + " kalori yaktınız.");
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
        finish();
    }
}