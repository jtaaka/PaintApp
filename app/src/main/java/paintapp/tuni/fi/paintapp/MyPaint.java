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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MyPaint extends View {

    public static final int DEFAULT_BRUSH_COLOR = Color.BLACK;
    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
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
    private float brushSize;
    private int backgroundColor;


    public MyPaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        Debug.loadDebug(context);

        myPaint = new Paint();
        myPath = new Path();

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void initialize(int width, int height) {
        myBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitMap);
        setBrushColor(DEFAULT_BRUSH_COLOR);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setBrushSize(DEFAULT_STROKEWIDTH);
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
        brushSize = width;
        myPaint.setStrokeWidth(width);
    }

    public float getBrushSize() {
        return brushSize;
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

        //canvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);

        myCanvas.drawColor(backgroundColor);
        myCanvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);

        canvas.drawColor(backgroundColor);
        canvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);

        for (PaintPath path : paths) {
            myPaint.setColor(path.brushColor);
            myPaint.setStrokeWidth(path.brushSize);
            myPaint.setMaskFilter(null);

            if (path.blur)
                myPaint.setMaskFilter(blur);

            myCanvas.drawPath(path.path, myPaint);
            canvas.drawPath(path.path, myPaint);
        }
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
        myBitMap = Bitmap.createBitmap(myBitMap.getWidth(),myBitMap.getHeight(), Bitmap.Config.ARGB_8888);

        setNormalBrush();
        setBrushColor(DEFAULT_BRUSH_COLOR);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setBrushSize(DEFAULT_STROKEWIDTH);

        invalidate();
    }

    public void undoLastPath() {
        if (!paths.isEmpty()) {
            paths.remove(paths.get(paths.size() - 1));
            invalidate();
        }
    }

    public void setImage(Bitmap image) {
        myBitMap = Bitmap.createBitmap(image.copy(Bitmap.Config.ARGB_8888, true));

        //myCanvas.setBitmap(myBitMap);

        paths.clear();
        myPath.reset();
        //invalidate();
    }

    public Bitmap getImage() {
        return myBitMap;
    }
}
