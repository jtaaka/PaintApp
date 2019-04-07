package paintapp.tuni.fi.paintapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends MyBaseActivity {

    private MyPaint myPaint;
    private int bgColor = Color.BLACK;
    private int brushColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Debug.loadDebug(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        myPaint = findViewById(R.id.paint);
        myPaint.initialize(width, height);

        Debug.print(TAG, "onCreate()", "Alive?",1, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.reset:
                myPaint.reset();
                return true;
            case R.id.background:
                chooseBgColor();
                return true;
            case R.id.brushColor:
                chooseBrushColor();
                return true;
        }

        return false;
    }

    public void chooseBgColor() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                bgColor = color;
                myPaint.fillCanvas(color);
            }
        });

        colorPicker.show();
    }

    public void chooseBrushColor() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, brushColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                brushColor = color;
                myPaint.changeBrushColor(color);
            }
        });

        colorPicker.show();
    }
}
