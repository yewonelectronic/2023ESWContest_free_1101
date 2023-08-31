package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

//Draw_Activity.java: 목적) 침대 크기 설정
// 기능) 웹캠 화면위에 점을 찍고 4개의 점 x, y 좌표값 을 DB에 저장
public class Draw_Activity extends AppCompatActivity {
    int i=0;

    private FrameLayout drawingContainer;
    //private TextView coordinatesTextView;


    private void FragmentView(int fragment){

        //Main_Fragment 즉 일반 카메라 웹뷰를 띄운다.
        if (fragment==1){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Main_Fragment fragment1 = new Main_Fragment();
            transaction.replace(R.id.fragment_container, fragment1);
            transaction.commit();
        }
    }
    float x1=0;
    float x2=0;
    float x3=0;
    float x4=0;
    float y1=0;
    float y2=0;
    float y3=0;
    float y4=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_layout);
        Button btn = findViewById(R.id.btn);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FragmentView(1);
        //4개 점의 x, y 좌표값 총 8개의 값을 저장할 파이어베이스 ref에 지정
        DatabaseReference refX1=database.getReference("BED/X1");
        DatabaseReference refX2=database.getReference("BED/X2");
        DatabaseReference refX3=database.getReference("BED/X3");
        DatabaseReference refX4=database.getReference("BED/X4");
        DatabaseReference refY1=database.getReference("BED/Y1");
        DatabaseReference refY2=database.getReference("BED/Y2");
        DatabaseReference refY3=database.getReference("BED/Y3");
        DatabaseReference refY4=database.getReference("BED/Y4");

        drawingContainer = findViewById(R.id.drawing);

        //터치 이벤트 감지
        drawingContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            //터치가 인식될 때
            public boolean onTouch(View v, MotionEvent event) {
                //터치가 인식 된 점의 x,y를 받아서 float값으로 변수 저장
                float x = event.getX();
                float y = event.getY();
                //터치된 순서대로 4개의 점을 해당 DEB ref 값으로 저장
                if(i==0){
                    x1=x;
                    y1=y;

                }
                else if(i==3){
                    x2=x;
                    y2=y;

                }
                else if(i==5){
                    x3=x;
                    y3=y;

                }
                else if(i==7){
                    x4=x;
                    y4=y;

                }
                i+=1;

                // 클릭한 좌표에 점을 그린다.(해당 함수 설명은 밑에 있다.)
                drawPoint((int)x, (int)y);


                return true;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refX1.setValue((int)x1);
                refY1.setValue((int)y1);
                refX2.setValue((int)x2);
                refY2.setValue((int)y2);
                refX3.setValue((int)x3);
                refY3.setValue((int)y3);
                refX4.setValue((int)x4);
                refY4.setValue((int)y4);
            }
        });

    }


    // 클릭한 좌표에 점을 그리는 함수이다.
    private void drawPoint( float x, float y) {
        //PointView 클래스 이용
        PointView pointView = new PointView(this);
        //params를 조절하여 크기를 맞춤
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);
        //중심위치로 위치 조정
        params.leftMargin = (int) x - 50;
        params.topMargin = (int) y - 50;

        pointView.setLayoutParams(params);
        //해당 좌료에 점을 표시한다.
        drawingContainer.addView(pointView);

    }


}


