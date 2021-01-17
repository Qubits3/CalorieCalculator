package com.example.caloriecalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    SharedPreferences sharedP;
    EditText nameL, surnameL, heightL, weightL;
    private RadioGroup cinsiyetRG;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setTitle("Kişisel Bilgilerim");

        sharedP = this.getSharedPreferences("com.example.caloriecalculator", Context.MODE_PRIVATE);

        //Referanslar
        nameL = findViewById(R.id.nameLabel);
        surnameL = findViewById(R.id.surnameLabel);
        heightL = findViewById(R.id.heightLabel);
        weightL = findViewById(R.id.weightLabel);
        cinsiyetRG = findViewById(R.id.radio);
        img = findViewById(R.id.mainimg);


        String savedName = sharedP.getString("isim", "");
        String savedSurname = sharedP.getString("soyisim", "");
        String savedHeight = sharedP.getString("boy", "");
        String savedWeight = sharedP.getString("kilo", "60");
        String savedGender = sharedP.getString("cinskey", "");
        int checkedId = sharedP.getInt("Gender", 0);
        nameL.setText(savedName);
        surnameL.setText(savedSurname);
        heightL.setText(savedHeight);
        weightL.setText(savedWeight);
        cinsiyetRG.check(checkedId);

        if (savedGender.equals("Erkek")) {
            img.setImageResource(R.drawable.male);
        } else if (savedGender.equals("Kadın")) {
            img.setImageResource(R.drawable.female);
        } else {
            img.setImageResource(R.drawable.notselected);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cinsiyetRG.setOnCheckedChangeListener((group, checkedId) -> {

            RadioButton rb = findViewById(checkedId);
            int index = cinsiyetRG.indexOfChild(rb);

            SharedPreferences.Editor editor = sharedP.edit();
            editor.putInt("Gender", index);
            editor.apply();

            switch (index) {

                case 1:
                    img.setImageResource(R.drawable.male);
                    break;
                case 2:
                    img.setImageResource(R.drawable.female);
                    break;
                default:
                    img.setImageResource(R.drawable.notselected);
                    break;
            }
        });
    }

    public void onayla(View view) { //onayla butonuna tıklandığında

        int selectedId = cinsiyetRG.getCheckedRadioButtonId();
        RadioButton cinsiyetRB = findViewById(selectedId);

        //TextInputLayoutlara girilen verilerin değişkenlere alınması
        String name = nameL.getText().toString();
        String surname = surnameL.getText().toString();
        String height = heightL.getText().toString();
        String weight = weightL.getText().toString();


        String cinsiyet = ""; //shared' key'inin boş dönmemesi için nesne burada boş oluşturuldu.
        if (cinsiyetRB != null) {
            cinsiyet = cinsiyetRB.getText().toString();
        }


        if (name.isEmpty() || surname.isEmpty() || height.isEmpty() || weight.isEmpty() || cinsiyet.equals("")) {

            Toast.makeText(this, "Lütfen Bilgilerinizi Doldurunuz!", Toast.LENGTH_LONG).show();
        } else {    //veriler sharedpreferencesa aktarılıyor

            SharedPreferences.Editor editor = sharedP.edit();
            editor.putString("isim", name);  //isim sharedda keyi ile saklanır
            editor.putString("soyisim", surname);
            editor.putString("boy", height);
            editor.putString("kilo", weight);
            editor.putString("cinskey", cinsiyet);
            editor.putInt("Gender", cinsiyetRB.getId());

            editor.apply(); //Veriler sharedprefe aktarılır.

            Toast.makeText(this, "Bilgileriniz Başarıyla Kaydedildi!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
