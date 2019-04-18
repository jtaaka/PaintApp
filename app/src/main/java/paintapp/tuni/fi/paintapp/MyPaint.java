package paintapp.tuni.fi.paintapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyPaint extends View {

    public static final int DEFAULT_BRUSH = Color.BLACK;
    public static final int DEFAULT_BACKGROUND = Color.WHITE;
    public static final float DEFAULT_STROKEWIDTH = 10;

    private Paint myPaint;
    private Canvas myCanvas;
    private Bitmap myBitMap;
    private Path myPath;
    private Paint myBitMapPaint = new Paint(Paint.DITHER_FLAG);
    private ArrayList<PaintPath> paths = new ArrayList<>();

    private MaskFilter blur;
    private boolean blurBrush = false;
    private boolean normalBrush = true;
    private int brushColor;
    private int backgroundColor;

    public MyPaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Debug.loadDebug(context);

        myPaint = new Paint();
        myPath = new Path();

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(DEFAULT_STROKEWIDTH);
        setBrushColor(DEFAULT_BRUSH);
    }

    public void initialize(int width, int height) {
        myBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitMap);
        myCanvas.drawColor(DEFAULT_BACKGROUND);
    }

    public void setBlurBrush() {
        blurBrush = true;
        normalBrush = false;

        blur = new BlurMaskFilter(myPaint.getStrokeWidth() / 6F, BlurMaskFilter.Blur.NORMAL);
    }

    public void setNormalBrush() {
        normalBrush = true;
        blurBrush = false;
    }

    public void setBrushSize(float width) {
        myPaint.setStrokeWidth(width);
    }

    public float getBrushSize() {
        return myPaint.getStrokeWidth();
    }

    public void setBrushColor(int color) {
        brushColor = color;
    }

    public int getBrushColor() {
        return brushColor;
    }

    public void setBackgroundColor(int color) {
        myCanvas.drawColor(color);
        backgroundColor = color;
        invalidate();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        myCanvas.drawColor(backgroundColor);

        for (PaintPath path : paths) {
            myPaint.setColor(path.brushColor);
            myPaint.setStrokeWidth(path.brushSize);
            myPaint.setMaskFilter(null);

            if (path.blur)
                myPaint.setMaskFilter(blur);

            myCanvas.drawPath(path.path, myPaint);
        }

        canvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);
        canvas.restore();
    }

    public void drawPath(float xPos, float yPos) {
        myPath = new Path();
        PaintPath path = new PaintPath(getBrushSize(), getBrushColor(), getBackgroundColor(), blurBrush, myPath);
        paths.add(path);
        myPath.reset();
        myPath.moveTo(xPos, yPos);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath(xPos, yPos);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                myPath.lineTo(xPos, yPos);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }

        return true;
    }

    public void reset() {
        myPath.reset();
        paths.clear();

        setNormalBrush();
        setBrushColor(DEFAULT_BRUSH);
        setBackgroundColor(DEFAULT_BACKGROUND);
        myPaint.setStrokeWidth(DEFAULT_STROKEWIDTH);

        invalidate();
    }
}
