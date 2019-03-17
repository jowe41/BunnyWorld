package com.example.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.bunnyworld.Editor.selectedShape;

public class CustomViewEditor extends View {

    ArrayList<Shape> shapesOnCurrentPage, shapesOnPossession;
    int viewWidth, viewHeight;
    final double DIVISION = 0.75;
    Paint redOutlinePaint;
    int count = 0;

    public CustomViewEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        redOutlinePaint = new Paint();
        redOutlinePaint.setColor(Color.RED);
        redOutlinePaint.setStyle(Paint.Style.STROKE);
        redOutlinePaint.setStrokeWidth(5.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (count == 0) {
            Editor.putDividingBoundary((float)DIVISION*viewHeight);
            count = 1;
        }
        shapesOnCurrentPage = Editor.getShapesOnCurPage();
        for (int i = 0; i < shapesOnCurrentPage.size(); i++) {
            shapesOnCurrentPage.get(i).draw(canvas, null);
        }

        if (selectedShape != null) {
            selectedShape.drawOutline(canvas);
        }

        shapesOnPossession = Editor.getShapesOnPos();
        for (int i = 0; i < shapesOnPossession.size(); i++) {
            shapesOnPossession.get(i).draw(canvas, null);
        }
        canvas.drawLine(0.0f, (float)DIVISION*viewHeight, viewWidth, (float)DIVISION*viewHeight, redOutlinePaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
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
        shapesOnCurrentPage = Editor.getShapesOnCurPage();
        shapesOnPossession = Editor.getShapesOnPos();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Editor.noSelect();
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
                            curShape = new Shape (shapesOnPossession.get(i));
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
                    curShape.setX(x2);
                    curShape.setY(y2);
                    moved = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (find) {
                    pressUpTime = System.currentTimeMillis();
                    x3 = event.getX();
                    y3 = event.getY();
                    if (onPage && pressUpTime - pressDownTime <= MAX_CLICK_DURATION
                            && Math.abs(x3 - x1) <= MAX_CLICK_DISTANCE && Math.abs(y3 - y1) <= MAX_CLICK_DISTANCE) {
                        Editor.select(curShape);
                    } else if (moved) {
                        if (curShape.getY() <= DIVISION * viewHeight) {
                            curShape.limitTopHeight((float)DIVISION * viewHeight);
                            if (!onPage) {
                                Editor.moveToCurPage(curShape);
                            }
                        } else {
                            if (onPage) {
                                Editor.curPage.removeShape(curShape);
                            }
                        }
                    }
                }
                if (moved) {
                    Shape newShape = new Shape (curShape);
                    newShape.setID(curShape.getID());
                    newShape.setName(curShape.getName());
                    if (onPage) {
                        newShape.setX(x0);
                        newShape.setY(y0);
                    } else {
                        newShape.setPageName("");
                    }
                    Editor.shapeBackup.push(newShape);
                }
                find = false;
                moved = false;
        }
        invalidate();
        return true;
    }


}
