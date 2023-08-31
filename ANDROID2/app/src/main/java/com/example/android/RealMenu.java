package com.example.android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//RealMenu.java: 목적) 4개의 버튼으로 메뉴 구현
public class RealMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realmenu);

        Button sensorButton = findViewById(R.id.DB1);
        Button WebButton = findViewById(R.id.Web);
        Button menuButton = findViewById(R.id.graph);
        Button setting = findViewById(R.id.setting);
        Button camButton=findViewById(R.id.DB2);
        //Data 버튼을 클릭하면 SubActivity로 Intent
        sensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Sensor_Activity.class);
                startActivity(intent);
            }
        });
        //Cam 버튼을 클릭하면 Web으로 Intent
        WebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Web.class);
                startActivity(intent);

            }
        });
        //Graph 버튼을 클릭하면 Graph_Activity로 Intent
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Graph_Activity.class);

                startActivity(intent);
            }
        });
        //BedSetting 버튼을 클릭하면 Draw_Activity로 Intent
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Draw_Activity.class);
                startActivity(intent);
            }
        });
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                startActivity(intent);
            }
        });
    }
}
