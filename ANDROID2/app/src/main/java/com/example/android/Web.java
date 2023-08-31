package com.example.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Web extends AppCompatActivity {
    private final int Fragment_1 = 1;
    private final int Fragment_2 = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        //일반카메라 버튼이 클릭되면 fragment_1 띄우기
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentView(Fragment_1);

            }
        });
        //적외선 카메라 버튼이 클릭되면 fragment_2 띄우기
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentView(Fragment_2);

            }
        });
    }

    private void FragmentView(int fragment){

        //FragmentTransactiom를 이용해 프래그먼트를 사용
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment){
            case 1:
                // Main_Fragment() 호출
                Main_Fragment fragment1 = new Main_Fragment();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();
                break;

            case 2:
                // Sub_Fragment() 호출
                Sub_Fragment fragment2 = new Sub_Fragment();
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();
                break;

        }
    }


}
