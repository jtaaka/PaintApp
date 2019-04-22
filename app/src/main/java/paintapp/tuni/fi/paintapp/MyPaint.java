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

/**
 * Class that defines the canvas and logic used to draw on it.
 *
 * @author  Juho Taakala (juho.taakala@tuni.fi)
 * @version 20190422
 * @since   1.8
 */
public class MyPaint extends View {

    /**
     * Sets default brush color.
     */
    public static final int DEFAULT_BRUSH_COLOR = Color.BLACK;

    /**
     * Sets default background color.
     */
    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    /**
     * Sets default stroke width for brush.
     */
    public static final float DEFAULT_STROKEWIDTH = 10;

    /**
     * Defines Paint class.
     */
    private Paint myPaint;

    /**
     * Defines Canvas class.
     */
    private Canvas myCanvas;

    /**
     * Defines Bitmap class.
     */
    private Bitmap myBitMap;

    /**
     * Defines Path class.
     */
    private Path myPath;

    /**
     * Defines Paint used with Bitmap.
     */
    private Paint myBitMapPaint = new Paint(Paint.DITHER_FLAG);

    /**
     * Defines list of paths used to save drawing information.
     */
    private ArrayList<PaintPath> paths = new ArrayList<>();

    /**
     * Defines MaskFilter that is used with blur brush.
     */
    private MaskFilter blur;

    /**
     * Defines if blur brush is used or not.
     */
    private boolean blurBrush = false;

    /**
     * Defines is normal brush is used or not.
     */
    private boolean normalBrush = true;

    /**
     * Defines the brush color.
     */
    private int brushColor;

    /**
     * Defines the brush size.
     */
    private float brushSize;

    /**
     * Defines the background color.
     */
    private int backgroundColor;

    /**
     * Constructs MyPaint class and creates new Paint and Path objects.
     *
     * @param context Application context.
     * @param attrs Collection of attributes.
     */
    public MyPaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        myPaint = new Paint();
        myPath = new Path();
    }

    /**
     * Initializes paint and canvas and sets default values.
     *
     * @param width The absolute width of the available display size in pixels.
     * @param height The absolute height of the available display size in pixels.
     */
    public void initialize(int width, int height) {
        myBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitMap);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setAntiAlias(true);
        setBrushColor(DEFAULT_BRUSH_COLOR);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setBrushSize(DEFAULT_STROKEWIDTH);
    }

    /**
     * Sets blur brush.
     */
    public void setBlurBrush() {
        blurBrush = true;
        normalBrush = false;

        blur = new BlurMaskFilter(myPaint.getStrokeWidth() / 6F, BlurMaskFilter.Blur.NORMAL);
    }

    /**
     * Sets normal brush.
     */
    public void setNormalBrush() {
        normalBrush = true;
        blurBrush = false;
    }

    /**
     * Sets brush size.
     *
     * @param width Width of brush.
     */
    public void setBrushSize(float width) {
        brushSize = width;
        myPaint.setStrokeWidth(width);
    }

    /**
     * Gets the size of current brush.
     *
     * @return the size of brush.
     */
    public float getBrushSize() {
        return brushSize;
    }

    /**
     * Sets the brush color.
     *
     * @param color Color value of brush.
     */
    public void setBrushColor(int color) {
        brushColor = color;
    }

    /**
     * Gets the color of current brush.
     *
     * @return the color of brush.
     */
    public int getBrushColor() {
        return brushColor;
    }

    /**
     * Sets the background color.
     *
     * @param color Color value of background color.
     */
    public void setBackgroundColor(int color) {
        myCanvas.drawColor(color);
        backgroundColor = color;
        invalidate();
    }

    /**
     * Gets the color of background.
     *
     * @return the color of background.
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(myBitMap, 0, 0, myBitMapPaint);
        myCanvas.drawColor(getBackgroundColor());

        for (PaintPath path : paths) {
            myPaint.setColor(path.brushColor);
            myPaint.setStrokeWidth(path.brushSize);
            myPaint.setMaskFilter(null);

            if (path.blur) myPaint.setMaskFilter(blur);

            myCanvas.drawPath(path.path, myPaint);
            canvas.drawPath(path.path, myPaint);
        }
    }

    /**
     * Sets the starting point for drawing and adds new path with current paint settings.
     *
     * @param xPos X position touched on screen.
     * @param yPos Y position touched on screen.
     */
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

    /**
     * Clears drawn paths, creates new empty bitmap and canvas and sets default paint settings.
     */
    public void reset() {
        myPath.reset();
        paths.clear();

        myBitMap = Bitmap.createBitmap(myBitMap.getWidth(), myBitMap.getHeight(), Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitMap);

        setNormalBrush();
        setBrushColor(DEFAULT_BRUSH_COLOR);
        setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
        setBrushSize(DEFAULT_STROKEWIDTH);

        invalidate();
    }

    /**
     * Clears last drawn path.
     */
    public void undoLastPath() {
        if (!paths.isEmpty()) {
            paths.remove(paths.get(paths.size() - 1));
            invalidate();
        }
    }

    /**
     * Sets image from user's gallery to canvas.
     *
     * @param image Image from user's gallery.
     */
    public void setImage(Bitmap image) {
        myPath.reset();
        paths.clear();

        myBitMap = image.copy(Bitmap.Config.ARGB_8888, true);
        myCanvas = new Canvas(myBitMap);
        invalidate();
    }

    /**
     * Gets drawn image.
     *
     * @return bitmap of currently drawn canvas.
     */
    public Bitmap getImage() {
        return myBitMap;
    }
}
