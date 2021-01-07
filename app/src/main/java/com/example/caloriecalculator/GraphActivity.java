package com.example.caloriecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {

    ImageView mondaypic;
    ImageView tuesdaypic;
    ImageView wednesdaypic;
    ImageView thursdaypic;
    ImageView fridaypic;
    ImageView saturdaypic;
    ImageView sundaypic;
    TextView verim;
    SharedPreferences sharedPreferences;
    int height;
    int day;
    //int oncekigun = 0;
    //String bironcekigun;
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

        storeDay();

        mondaypic = findViewById(R.id.mondaypic);
        tuesdaypic = findViewById(R.id.tuesdaypic);
        wednesdaypic = findViewById(R.id.wednesdaypic);
        thursdaypic = findViewById(R.id.thursdaypic);
        fridaypic = findViewById(R.id.fridaypic);
        saturdaypic = findViewById(R.id.saturdaypic);
        sundaypic = findViewById(R.id.sundaypic);
        verim = findViewById(R.id.verim);

        mondayCal = sharedPreferences.getString("2","");
        tuesdayCal = sharedPreferences.getString("3","");
        wednesdayCal = sharedPreferences.getString("4","");
        thursdayCal = sharedPreferences.getString("5","");
        fridayCal = sharedPreferences.getString("6","");
        saturdayCal = sharedPreferences.getString("7","");
        sundayCal = sharedPreferences.getString("1","");
        //bironcekigun = sharedPreferences.getString(String.valueOf(day - 1), "");
/*
        if (!bironcekigun.equals("0") && !bironcekigun.equals("")) {
            oncekigun = Integer.parseInt(bironcekigun.substring(0, bironcekigun.indexOf(".")));
            oncekigun /= 5;
        }
        */
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
            height /=5;
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

        day = sharedPreferences.getInt("day",1);

            verimtotext();


    }

        private void verimtotext() {

            if (day == 2 && !mondayCal.equals("0") && !mondayCal.equals("")) {
                height = Integer.parseInt(mondayCal.substring(0, mondayCal.indexOf(".")));

                mondaypic.setPadding(5,5,5,5);
                mondaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 3 && !tuesdayCal.equals("0") && !tuesdayCal.equals("")) {
                height = Integer.parseInt(tuesdayCal.substring(0, tuesdayCal.indexOf(".")));

                tuesdaypic.setPadding(5,5,5,5);
                tuesdaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 4 && !wednesdayCal.equals("0") && !wednesdayCal.equals("")) {
                height = Integer.parseInt(wednesdayCal.substring(0, wednesdayCal.indexOf(".")));

                wednesdaypic.setPadding(5,5,5,5);
                wednesdaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 5 && !thursdayCal.equals("0") && !thursdayCal.equals("")) {
                height = Integer.parseInt(thursdayCal.substring(0, thursdayCal.indexOf(".")));
                thursdaypic.setPadding(5,5,5,5);
                thursdaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 6 && !fridayCal.equals("0") && !fridayCal.equals("")) {
                height = Integer.parseInt(fridayCal.substring(0, fridayCal.indexOf(".")));

                fridaypic.setPadding(5,5,5,5);
                fridaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 7 && !saturdayCal.equals("0") && !saturdayCal.equals("")) {
                height = Integer.parseInt(saturdayCal.substring(0, saturdayCal.indexOf(".")));

                saturdaypic.setPadding(5,5,5,5);
                saturdaypic.setBackgroundColor(Color.BLACK);
            } else if (day == 1 && !sundayCal.equals("0") && !sundayCal.equals("")) {
                height = Integer.parseInt(sundayCal.substring(0, sundayCal.indexOf(".")));

                sundaypic.setPadding(5,5,5,5);
                sundaypic.setBackgroundColor(Color.BLACK);
            }
            if(height > 500){
                verim.setText("Bugün oldukça iyisiniz.");
            }else if(height > 250){
                verim.setText("Bugünlük yeterli bir efor.");
            }else if(height > 100){
                verim.setText("Bugün fena değilsiniz.");
            }else if(height <= 100){
                verim.setText("Bugün çok kötüsünüz.");
            }
        }

    private void storeDay() {
        sharedPreferences.edit()
                .putInt("day", getDay())
                .apply();
    }
    public int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }
}
