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
    int height;
    String mondayCal;
    String tuesdayCal;
    String wednesdayCal;
    String thursdayCal;
    String fridayCal;
    String saturdayCal;
    String sundayCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setTitle("Grafik");

        sharedPreferences = getSharedPreferences("com.example.caloriecalculator", MODE_PRIVATE);

        mondaypic = findViewById(R.id.mondaypic);
        tuesdaypic = findViewById(R.id.tuesdaypic);
        wednesdaypic = findViewById(R.id.wednesdaypic);
        thursdaypic = findViewById(R.id.thursdaypic);
        fridaypic = findViewById(R.id.fridaypic);
        saturdaypic = findViewById(R.id.saturdaypic);
        sundaypic = findViewById(R.id.sundaypic);

        mondayCal = sharedPreferences.getString("2","");
        tuesdayCal = sharedPreferences.getString("3","");
        wednesdayCal = sharedPreferences.getString("4","");
        thursdayCal = sharedPreferences.getString("5","");
        fridayCal = sharedPreferences.getString("6","");
        saturdayCal = sharedPreferences.getString("7","");
        sundayCal = sharedPreferences.getString("1","");


        //day = sharedPreferences.getInt("day",1);

        //String bironcekigun = sharedPreferences.getString(String.valueOf(day - 1), "");

        if (!mondayCal.equals("0") && !mondayCal.equals("")){
            height = Integer.parseInt(mondayCal.substring(0, mondayCal.indexOf(".")));
            height /= 5;
            mondaypic.getLayoutParams().height = height;
        }if (!tuesdayCal.equals("0") && !tuesdayCal.equals("")){
            height = Integer.parseInt(tuesdayCal.substring(0, tuesdayCal.indexOf(".")));
            height /= 5;
            tuesdaypic.getLayoutParams().height = height;
        }if (!wednesdayCal.equals("0") && !wednesdayCal.equals("")){
            height = Integer.parseInt(wednesdayCal.substring(0, wednesdayCal.indexOf(".")));
            height /= 5;
            wednesdaypic.getLayoutParams().height = height;
        }if (!thursdayCal.equals("0") && !thursdayCal.equals("")){
            height = Integer.parseInt(thursdayCal.substring(0, thursdayCal.indexOf(".")));
            height /= 5;
            thursdaypic.getLayoutParams().height = height;
        }if (!fridayCal.equals("0") && !fridayCal.equals("")){
            height = Integer.parseInt(fridayCal.substring(0, fridayCal.indexOf(".")));
            height /= 5;
            fridaypic.getLayoutParams().height = height;
        }if (!saturdayCal.equals("0") && !saturdayCal.equals("")){
            height = Integer.parseInt(saturdayCal.substring(0, saturdayCal.indexOf(".")));
            height /= 5;
            saturdaypic.getLayoutParams().height = height;
        }if (!sundayCal.equals("0") && !sundayCal.equals("")){
            height = Integer.parseInt(sundayCal.substring(0, sundayCal.indexOf(".")));
            height /= 5;
            sundaypic.getLayoutParams().height = height;
        }


    }
/*
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

    }
*/
}