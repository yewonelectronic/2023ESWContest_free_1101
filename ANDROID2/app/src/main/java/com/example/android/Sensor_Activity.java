package com.example.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
//import android.support.v7.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;

//Sensor_Activity.java 기능: bpm, temp값 관련 data 확인 및 onoff
public class Sensor_Activity extends AppCompatActivity{
    TextView warning;
    TextView on_off;
    TextView tmp;
    Switch bpm;
    Button temp;
    static final String CHANNEL_ID = "channelId";
    static final int notificationId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //각 정보에 맞는 파이어베이스 DB ref 값 지정


        DatabaseReference refbpm=database.getReference("BPM/ONOFF");//BPM 감지 ON OFF(송신)
        DatabaseReference reftemp1=database.getReference("temp/ONOFF");//BPM 감지 ON OFF(송신)
        DatabaseReference reftemp2=database.getReference("temp/TEMP");//온도(수신)
        DatabaseReference refBPM=database.getReference("BPM/warningBPM");//80이하의 BPM이면 1(수신)


        warning=findViewById(R.id.warning);
        on_off=findViewById(R.id.on_off);

        bpm=findViewById(R.id.bpm);
        temp=findViewById(R.id.temp);
        tmp=findViewById(R.id.tmp);

        ////* 위험 BPM 휴대폰 알림*////
        //BPM ref의 값이 변화했을 때 내부 함수 실행(warning bpm을 알린다.)
        refBPM.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //BPM 값이 바뀌면 가져와서 value에 저장한다.
                Integer value = dataSnapshot.getValue(Integer.class);
                //value<=80이면 위험 알림을 보낸다.
                if(value <=80){
                    //Toast.makeText(getApplicationContext(), "BPM이 80 이하입니다.", Toast.LENGTH_LONG).show();
                    createNotificationChannel ();
                    //휴대폰 푸시알림을 보낸다.
                    createNotification1 ();
                    //Warning BPM을 출력한다.
                    warning.setText("WARNING BPM:" + value );
                }
                else{
                    Toast.makeText(getApplicationContext(), "BPM 정상입니다.", Toast.LENGTH_LONG).show();
                    warning.setText(" BPM:" + value );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                warning.setText("error:" + databaseError.toException());
            }
        });

        //bpm ref에 접근한다(DB 값을 확인하고 bpm이 1이면 bpm on을 스위치 화면에 띄운다.)>>bpm 측정 동작 여부
        refbpm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String value = dataSnapshot.getValue(String.class);
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value==1){
                    (bpm).setChecked(true);
                }
                else if (value==0){
                    (bpm).setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                warning.setText("error:" + databaseError.toException());
            }
        });

        ////*각 스위치가 true로 바뀌면 파이어베이스 DB 저장*////
        //스위치가 켜지면 night ref에 접근하여 값을 1로 변경

        bpm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //refbpm 주소의 값 1로 변경
                    refbpm.setValue(1);
                } else {
                    //bpmflip 주소의 값 0으로 변경
                    refbpm.setValue(0);

                }
            }
        });
        //체온계 측정 버튼을 눌렀을 때
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //아두이노에 데이터 요청 하기위해서 값 1로 수정
                reftemp1.setValue(1);
                //temp 값이 변경되어 들어오면 내부 함수 실행
                reftemp2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //String value = dataSnapshot.getValue(String.class);
                        Float value = dataSnapshot.getValue(Float.class);
                        //초기값은 제외
                        if(value!=0){
                            String value2 = String.format("%.1f", value);
                            tmp.setText("온도"+ value2 );
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }
    //Notification 채넣 생성
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId = getString (R.string.notification_channel_id);
            String channelName = getString (R.string.notification_channel_name);
            String channelDes = getString (R.string.notification_channel_description);
            NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel =
                    new NotificationChannel (channelId //채널 ID
                            , channelName //채널 Name
                            , NotificationManager.IMPORTANCE_HIGH);//중요도 HIGH 부터 헤드업 알림
            notificationChannel.setDescription (channelDes);//채널설명
            notificationManager.createNotificationChannel (notificationChannel);
        }

    }
    //노티피케이션 생성
    private void createNotification1(){


        String channelId = getString (R.string.notification_channel_id);
        Intent intent = new Intent(getApplicationContext(), SubActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,notificationId ,intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder (this, channelId)
                        .setSmallIcon (R.drawable.picture)  //아이콘
                        .setContentTitle ("위험 알림") //노티피케이션 타이틀
                        .setContentText ("BPM이 80 이하입니다") //본문 텍스트
                        .setContentIntent(pendingIntent)
                        .setAutoCancel (true); //사용자가 탭하면 자동으로 알림을 삭제


        NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
        notificationManager.notify (0, notification.build ());
    }


}
