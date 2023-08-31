package com.example.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

//PointView: Draw_Activity에 사용되는 클래스(점 그리기)
public class PointView extends View {

    private Paint paint;

    public PointView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.RED); // 점의 색상을 red로 설정
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//canvas 열기
        int radius = 15; // 점의 반지름 15f로 설정
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, paint);
        //해당 크기의 원을 그려서 점을 찍는다.
    }
}
