package paintapp.tuni.fi.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyPaint extends View {

    public static final int DEFAULT_BRUSH = Color.BLACK;
    public static final int DEFAULT_BACKGROUND = Color.WHITE;

    private Paint myPaint;
    private Canvas myCanvas;
    private Bitmap myBitMap;
    private Path myPath;
    private Paint myBitMapPaint = new Paint(Paint.DITHER_FLAG);
    private List<Pair<Path, Integer>> pathColorList = new ArrayList<>();

    public MyPaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        myPaint = new Paint();
        myPath = new Path();

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(10);

        pathColorList.add(Pair.create(myPath, DEFAULT_BRUSH));
    }

    public void initialize(int width, int height) {
        myBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitMap);
        myCanvas.drawColor(DEFAULT_BACKGROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Pair<Path,Integer> pathColor : pathColorList) {
            myCanvas.drawPath(pathColor.first, myPaint);
            myPaint.setColor(pathColor.second);
            myCanvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);
            myCanvas.drawPath(myPath, myPaint);
            canvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                myPath.moveTo(xPos, yPos);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                myPath.lineTo(xPos, yPos);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
            default:
                return false;
        }

        return true;
    }

    public void fillCanvas(int color) {
        myCanvas.drawColor(color);
        invalidate();
    }

    public void changeBrushColor(int color) {
        pathColorList.add( Pair.create(myPath, color));
        myPath = new Path();
    }

    public void reset() {
        myPath.reset();
        pathColorList.clear();
        pathColorList.add( Pair.create(myPath, DEFAULT_BRUSH));
        myCanvas.drawColor(DEFAULT_BACKGROUND);
        invalidate();
    }
}
