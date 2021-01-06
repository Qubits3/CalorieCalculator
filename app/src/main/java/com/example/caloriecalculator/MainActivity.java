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
    TextView name;
    boolean profile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirstActivity aktivite = new FirstActivity();





        if(profile == aktivite.isFull){
            Intent first = new Intent(getApplicationContext(), FirstActivity.class);
            startActivity(first);
        }
        else {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            name = findViewById(R.id.ad);
            sharedP = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);

            String savedName = sharedP.getString("isim", "İsim bulunamadı");
            String cins = sharedP.getString("cinskey", "Belirsiz");

            name.setText("Merhaba " + savedName + " " + cins);

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