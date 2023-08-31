
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

//SubActivity.java 기능: 영상 관련한 센서 경고 받음( 낙상, 뒤집기, fall)
    public class SubActivity extends AppCompatActivity{
        TextView warning;
        TextView on_off;

        Switch night;
        Switch back;
        Switch fall;

        static final String CHANNEL_ID = "channelId";
        static final int notificationId = 0;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.nexttwo);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //각 정보에 맞는 파이어베이스 DB ref 값 지정

            DatabaseReference refnight=database.getReference("ONOFF/night");//야간모드 ON OFF(송신)
            DatabaseReference refback=database.getReference("ONOFF/back");//뒤집기 감지 ON OFF(송신)
            DatabaseReference refflip=database.getReference("ONOFF/fall");//낙상 감지  ON OFF(송신)
            DatabaseReference refwflip=database.getReference("BED/fall");//낙상감지되면 1(수신)
            DatabaseReference refwback=database.getReference("BED/back");//낙상감지되면 1(수신)

            warning=findViewById(R.id.warning);
            on_off=findViewById(R.id.on_off);
            night=findViewById(R.id.night);
            back=findViewById(R.id.back);
            fall=findViewById(R.id.flip);


            refwback.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //BPM 값이 바뀌면 가져와서 value에 저장한다.
                    Integer value = dataSnapshot.getValue(Integer.class);

                    //value<=80이면 위험 알림을 보낸다.
                    if(value==1){
                        //Toast.makeText(getApplicationContext(), "뒤집기 감지", Toast.LENGTH_LONG).show();
                        createNotificationChannel ();
                        //휴대폰 푸시알림을 보낸다.
                        createNotification2 ();
                        //Warning BPM을 출력한다.
                        on_off.setText("뒤집기 감지");
                        //on_off.setTextColor(Color.parseColor("#AAA"));//빨간색으로!
                    }
                    else{
                        //Toast.makeText(getApplicationContext(), "BPM 정상입니다.", Toast.LENGTH_LONG).show();
                        //warning.setText(" BPM:" + value );
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    on_off.setText("error:" + databaseError.toException());
                }
            });
            refwflip.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //BPM 값이 바뀌면 가져와서 value에 저장한다.
                    Integer value = dataSnapshot.getValue(Integer.class);

                    //value<=80이면 위험 알림을 보낸다.
                    if(value==1){

                        createNotificationChannel ();
                        //휴대폰 푸시알림을 보낸다.
                        createNotification3 ();
                        //Warning BPM을 출력한다.
                        on_off.setText("낙상 감지");



                    }
                    else{

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    on_off.setText("error:" + databaseError.toException());
                }
            });
            ////*다시 Data 창에 들어갔을 때 이전 DB 저장값이 남아있도록 하기 위해 값을 가져온다.*////
            //night ref에 접근한다(DB 값을 확인하고 night가 1이면 야간모드 on을 스위치 화면에 띄운다.)
            refnight.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Integer value = dataSnapshot.getValue(Integer.class);
                    if (value==1){
                        (night).setChecked(true);
                    }
                    else if (value==0){
                        (night).setChecked(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    warning.setText("error:" + databaseError.toException());
                }
            });
            //back ref에 접근한다(DB 값을 확인하고 back이 1이면 뒤집기 on을 스위치 화면에 띄운다.)
            refback.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Integer value = dataSnapshot.getValue(Integer.class);
                    if (value==1){
                        (back).setChecked(true);
                    }
                    else if (value==0){
                        (back).setChecked(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    warning.setText("error:" + databaseError.toException());
                }
            });

            //flip ref에 접근한다(DB 값을 확인하고 flip이 1이면 낙상 on을 스위치 화면에 띄운다.)
            refflip.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Integer value = dataSnapshot.getValue(Integer.class);
                    if (value==1){
                        (fall).setChecked(true);
                    }
                    else if (value==0){
                        (fall).setChecked(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    warning.setText("error:" + databaseError.toException());
                }
            });


            ////*각 스위치가 true로 바뀌면 파이어베이스 DB 저장*////
            //스위치가 켜지면 night ref에 접근하여 값을 1로 변경
            night.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        //refnight 주소의 값 1로 변경
                        refnight.setValue(1);

                    } else {
                        //refnight 주소의 값 1로 변경
                        refnight.setValue(0);
                    }
                }
            });
            //스위치가 켜지면 back ref에 접근하여 값을 1로 변경
            back.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        //refnight 주소의 값 1로 변경
                        refback.setValue(1);

                    } else {
                        //refnight 주소의 값 0로 변경
                        refback.setValue(0);
                    }
                }
            });
            //스위치가 켜지면 flip ref에 접근하여 값을 1로 변경
            fall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        //refflip 주소의 값 1로 변경
                        refflip.setValue(1);
                    } else {
                        //refflip 주소의 값 0로 변경
                        refflip.setValue(0);

                    }
                }
            });
        }
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
        private void createNotification3(){

            //getApplicationContext()
            String channelId = getString (R.string.notification_channel_id);
            Intent intent = new Intent(getApplicationContext(), SubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,notificationId ,intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            //new Intent(getApplicationContext(),SubSubActivity.class)
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder (this, channelId)
                            .setSmallIcon (R.drawable.picture)  //아이콘
                            .setContentTitle ("위험 알림") //노티피케이션 타이틀
                            .setContentText ("낙상이 감지되었습니다.") //본문 텍스트
                            .setContentIntent(pendingIntent)
                            .setAutoCancel (true); //사용자가 탭하면 자동으로 알림을 삭제


            NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
            notificationManager.notify (0, notification.build ());
        }
        private void createNotification2(){

            //getApplicationContext()
            String channelId = getString (R.string.notification_channel_id);
            Intent intent = new Intent(getApplicationContext(), SubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,notificationId ,intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            //new Intent(getApplicationContext(),SubSubActivity.class)
            NotificationCompat.Builder notification =
                    new NotificationCompat.Builder (this, channelId)
                            .setSmallIcon (R.drawable.picture)  //아이콘
                            .setContentTitle ("위험 알림") //노티피케이션 타이틀
                            .setContentText ("뒤집기가 감지되었습니다.") //본문 텍스트
                            .setContentIntent(pendingIntent)
                            .setAutoCancel (true); //사용자가 탭하면 자동으로 알림을 삭제


            NotificationManager notificationManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
            notificationManager.notify (0, notification.build ());
        }

    }