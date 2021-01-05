package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

public class GraphActivity extends AppCompatActivity {

    ImageView mondaypic;
    ImageView tuesdaypic;
    ImageView wednesdaypic;
    ImageView thursdaypic;
    ImageView fridaypic;
    ImageView saturdaypic;
    ImageView sundaypic;
    SharedPreferences sharedPreferences;
    int day;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setTitle("Grafik");

        mondaypic = findViewById(R.id.mondaypic);
        tuesdaypic = findViewById(R.id.tuesdaypic);
        wednesdaypic = findViewById(R.id.wednesdaypic);
        thursdaypic = findViewById(R.id.thursdaypic);
        fridaypic = findViewById(R.id.fridaypic);
        saturdaypic = findViewById(R.id.saturdaypic);
        sundaypic = findViewById(R.id.sundaypic);

        sharedPreferences = getSharedPreferences("com.example.caloriecalculator", MODE_PRIVATE);
        day = sharedPreferences.getInt("day",1);
        height = sharedPreferences.getString("dailyCalorieOfAllDay","");
        height /= 5;
        daytograph();
    }

        public void daytograph(){
        if(day == 2){
            mondaypic.getLayoutParams().height = height;
        }else if(day == 3){
            tuesdaypic.getLayoutParams().height = height;
        }else if(day == 4){
            wednesdaypic.getLayoutParams().height = height;
        }else if(day == 5){
            thursdaypic.getLayoutParams().height = height;
        }else if(day == 6){
            fridaypic.getLayoutParams().height = height;
        }else if(day == 7){
            saturdaypic.getLayoutParams().height = height;
        }else if(day == 1){
            sundaypic.getLayoutParams().height = height;
        }else{

        }
    }

}