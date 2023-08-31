package com.example.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import com.example.android.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

//Graph_Activity.java: 목적) 30분 이내 BPM 값 Graph로 표시
//기능: 실시간 업데이트 기능 포함
public class Graph_Activity extends AppCompatActivity {
    //선 그래프
    private LineChart lineChart;
    float timeBPM0;
    float timeBPM1;
    float timeBPM2;
    float timeBPM3;
    float timeBPM4;
    float timeBPM5;
    float timeBPM6;
    float timeBPM7;
    float timeBPM8;
    float timeBPM9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //timeBPM값이 저장된 DB ref값을 timeBPM으로 지정
        DatabaseReference timeBPM=database.getReference("BPM/timeBPM");
        //DatabaseReference timeBPM0=database.getReference("BPM/timeBPM/time0");
        //DatabaseReference timeBPM1=database.getReference("BPM/timeBPM/time1");
        //DatabaseReference timeBPM2=database.getReference("BPM/timeBPM/time2");
        //DatabaseReference timeBPM3=database.getReference("BPM/timeBPM/time3");
        //DatabaseReference timeBPM4=database.getReference("BPM/timeBPM/time4");

        //Graph를 표시하기 위해 LineChart 위젯 사용
        lineChart = (LineChart) findViewById(R.id.chart);
        XAxis xAxis=lineChart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x축 데이터를 아래에 표시하기 위함
        xAxis.setLabelCount(9); //x축의 레이블을 4개로 설정
        xAxis.setTextColor(Color.rgb(118, 118, 118));//색상 설정
        xAxis.setSpaceMax(1f);
        xAxis.enableGridDashedLine(9, 24, 0);
        xAxis.setGranularity(1f);
        lineChart.setDrawGridBackground(true);//차트외곽선 진하게

        //timeBPM ref에 접근하기 위함
        timeBPM.addValueEventListener(new ValueEventListener() {
            @Override
            //timeBPM ref에 속한 값이 하나라도 변경되면 함수 실행
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lineChart.getDescription().setEnabled(true);
                //새로운 어레이 및 데이터 객체 생성
                ArrayList<Entry> entry_chart1 = new ArrayList<>();
                LineData chartData = new LineData();
                Group_Class BPM = dataSnapshot.getValue(Group_Class.class);
                //해당 DB ref의 time0-time4까지 해당하는 값을 float형태로 가져온다.
                //Group_Class의 get함수를 이용한다.
                timeBPM0 = (float) BPM.gettime0();
                timeBPM1 = (float) BPM.gettime1();
                timeBPM2 = (float) BPM.gettime2();
                timeBPM3 = (float) BPM.gettime3();
                timeBPM4 = (float) BPM.gettime4();
                timeBPM5 = (float) BPM.gettime5();
                timeBPM6 = (float) BPM.gettime6();
                timeBPM7 = (float) BPM.gettime7();
                timeBPM8 = (float) BPM.gettime8();
                timeBPM9 = (float) BPM.gettime9();
                //해당 데이터를 차트 생성 위한 어레이(entry_chart1)에 추가
                entry_chart1.add(new Entry(0,timeBPM0));
                entry_chart1.add(new Entry(1, timeBPM1));
                entry_chart1.add(new Entry(2, timeBPM2));
                entry_chart1.add(new Entry(3, timeBPM3));
                entry_chart1.add(new Entry(4, timeBPM4));
                entry_chart1.add(new Entry(5, timeBPM5));
                entry_chart1.add(new Entry(6, timeBPM6));
                entry_chart1.add(new Entry(7, timeBPM7));
                entry_chart1.add(new Entry(8, timeBPM8));
                entry_chart1.add(new Entry(9, timeBPM9));



                LineDataSet lineDataSet1 = new LineDataSet(entry_chart1, "LineGraph1"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.

                lineDataSet1.setColor(Color.RED); // LineDataSet의 색을 RED로 설정
                lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

                chartData.addDataSet(lineDataSet1); // 해당 LineDataSet1을 DataSet 에 넣는다.
                lineChart.setData(chartData); // 차트에 위의 DataSet을 넣는다.

                lineChart.invalidate(); // 차트를 없데이트 한다.
                lineChart.setTouchEnabled(false); // 차트 터치 disable


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });



    }
}