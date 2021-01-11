package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.Console;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedP = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);

        String savedName = sharedP.getString("isim", "İsim bulunamadı");
        String savedGender = sharedP.getString("cinskey", "Belirsiz");

       TextView yazi = findViewById(R.id.ad);

       yazi.setText(savedGender);



        if(savedGender == "Belirsiz"){
            Intent first = new Intent(getApplicationContext(), FirstActivity.class);
            startActivity(first);
        }

        if(savedName == "İsim bulunamadı"){
            MainActivity.this.finish();
            System.exit(0);
        }

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


}