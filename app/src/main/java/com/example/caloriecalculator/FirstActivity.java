package com.example.caloriecalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FirstActivity extends AppCompatActivity {

    SharedPreferences sharedP;
    EditText nameL;
    EditText surnameL;
    EditText heightL;
    EditText weightL;
    //private RadioButton erkek,kadin;



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
       /* erkek = findViewById(R.id.erkekRB);
        kadin = findViewById(R.id.kadinRB);



        erkek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean one_isChecked) {

            }
        });

        kadin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean two_isChecked) {

            }
        });*/

    }





    public void onayla(View view) { //onayla butonuna tıklandığında

        //TextInputLayoutlara girilen verilerin değişkenlere alınması
        String name = nameL.getText().toString();
        String surname = surnameL.getText().toString();
        String height = heightL.getText().toString();
        String weight = weightL.getText().toString();


        if (name.isEmpty() || surname.isEmpty() || height.isEmpty() || weight.isEmpty() ) {

            Toast.makeText(this, "Lütfen Bilgilerinizi Doldurunuz!", Toast.LENGTH_LONG).show();
        } else {    //veriler sharedpreferencesa aktarılıyor

            SharedPreferences.Editor editor = sharedP.edit();
            editor.putString("isim", name);  //isim sharedda keyi ile saklanır
            editor.putString("soyisim",surname);
            editor.putString("boy",height);
            editor.putString("kilo",weight);
            //cinsiyet eklenecek!!
            editor.apply(); //Veriler sharedprefe aktarılır.
            Toast.makeText(this, "Bilgileriniz Başarıyla Kaydedildi!", Toast.LENGTH_LONG).show();


            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }


}
/*
eklenmesi düşünülenler
girilen isimlerin yazılanyerde sürekli gözükmesi
cinsiyet seçimi sharede aktarılacak
xml in başına görsel eklenecek
bilgiler girildiğinde butona  tıklanıldığında bool ile true değeri verilecek ve bu sayfa ilk başta birdaha gösterilmeyecek

?eğer yapılabilinirse cinsiyete göre resim değişmesi yapılacak

+karakter sınırı ve özel karakter girilmemesi
+boş olursa hata mesajı verdirmesi
 */