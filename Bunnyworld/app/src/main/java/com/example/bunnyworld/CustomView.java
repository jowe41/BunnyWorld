package com.example.bunnyworld;

import java.util.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class CustomView extends View {
    ArrayList <Shape> shapesOnCurrentPage, shapesOnPossession;
    int viewWidth, viewHeight;
    final double DIVISION = 0.75;
    Shape movingShape = null;
    Paint redOutlinePaint;
    int count = 0;
    RectF rectBg1;
    RectF rectBg2;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        redOutlinePaint = new Paint();
        redOutlinePaint.setColor(Color.RED);
        redOutlinePaint.setStyle(Paint.Style.STROKE);
        redOutlinePaint.setStrokeWidth(5.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(Game.bgDrawable.getBitmap(), null, rectBg1, null);
        canvas.drawBitmap(Game.bgDrawable.getBitmap(), null, rectBg2, null);

        if (count == 0) {
            Game.putDividingBoundary((float)DIVISION*viewHeight);
            count = 1;
        }
        shapesOnCurrentPage = Game.getShapesOnCurPage();
        for (int i = 0; i < shapesOnCurrentPage.size(); i++) {
            shapesOnCurrentPage.get(i).draw(canvas, movingShape);
                    //.draw(canvas, movingShape);
        }

        shapesOnPossession = Game.getShapesOnPos();
        for (int i = 0; i < shapesOnPossession.size(); i++) {
            shapesOnPossession.get(i).draw(canvas, movingShape);
        }
        canvas.drawLine(0.0f, (float)DIVISION*viewHeight, viewWidth, (float)DIVISION*viewHeight, redOutlinePaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        rectBg1 = new RectF(0, 0, viewWidth / 2, (float)DIVISION * viewHeight);
        rectBg2 = new RectF(viewWidth / 2, 0, viewWidth, (float)DIVISION * viewHeight);
    }

    protected float getBoundary(){
        return (float) DIVISION*viewHeight;
    }

    float x0, y0, x1, y1, x2, y2, x3, y3;
    long pressDownTime, pressUpTime;
    final long MAX_CLICK_DURATION = 500;
    final float MAX_CLICK_DISTANCE = 10;
    Shape curShape;
    boolean find = false;
    boolean onPage = false;
    boolean moved = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressDownTime = System.currentTimeMillis();
                x1 = event.getX();
                y1 = event.getY();
                if (y1 <= DIVISION * viewHeight) {
                    for (int i = shapesOnCurrentPage.size() - 1; i >= 0; i--) {
                        if (shapesOnCurrentPage.get(i).contains(x1, y1)) {
                            curShape = shapesOnCurrentPage.get(i);
                            x0 = curShape.getX();
                            y0 = curShape.getY();
                            find = true;
                            onPage = true;
                            break;
                        }
                    }
                } else {
                    for (int i = shapesOnPossession.size() - 1; i >= 0; i--) {
                        if (shapesOnPossession.get(i).contains(x1, y1)) {
                            curShape = shapesOnPossession.get(i);
                            x0 = curShape.getX();
                            y0 = curShape.getY();
                            find = true;
                            onPage = false;
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (find) {
                    x2 = event.getX();
                    y2 = event.getY();
                    moved = curShape.move(x2, y2);
                    if (moved) {
                        movingShape = curShape;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (find) {
                    pressUpTime = System.currentTimeMillis();
                    x3 = event.getX();
                    y3 = event.getY();
                    if (onPage && pressUpTime - pressDownTime <= MAX_CLICK_DURATION
                            && Math.abs(x3 - x1) <= MAX_CLICK_DISTANCE && Math.abs(y3 - y1) <= MAX_CLICK_DISTANCE) {
                        curShape.clicked();
                    } else if (moved) {
                        if (curShape.getY() <= DIVISION * viewHeight) {

                            Game.moveToCurPage(curShape);
                            curShape.limitTopHeight((float)DIVISION * viewHeight);
                        } else {
                            Game.moveToPos(curShape);
                            curShape.limitBottomHeight((float)DIVISION * viewHeight);

                        }
                        for (int i = shapesOnCurrentPage.size() - 1; i >= 0; i--) {
                            if (!shapesOnCurrentPage.get(i).equals(curShape) && shapesOnCurrentPage.get(i).contains(x3, y3)) {
                                if (shapesOnCurrentPage.get(i).droppableBy(curShape)) {
                                    shapesOnCurrentPage.get(i).droppedBy(curShape);
                                } else {
                                    curShape.move(x0, y0);
                                }
                                break;
                            }
                        }
                    }
                    movingShape = null;
                    find = false;
                    moved = false;
                }
        }
        invalidate();
        return true;
    }


}
