package paintapp.tuni.fi.paintapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xw.repo.BubbleSeekBar;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends MyBaseActivity {

    private MyPaint myPaint;
    private int bgColor = Color.WHITE;
    private int brushColor = Color.BLACK;

    private DrawerLayout drawerLayout;
    private BubbleSeekBar bubbleSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bubbleSeekBar = findViewById(R.id.slider);
        bubbleSeekBar.setVisibility(View.INVISIBLE);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        myPaint = findViewById(R.id.paint);
        myPaint.initialize(width, height);

        drawerLayout = findViewById(R.id.drawer_layout);
        setupDrawerContent(findViewById(R.id.navigationView));

        Debug.loadDebug(this);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.reset:
                        myPaint.reset();
                        break;
                    case R.id.background:
                        chooseBgColor();
                        break;
                    case R.id.brushColor:
                        chooseBrushColor();
                        break;
                    case R.id.brushSize:
                        chooseBrushSize();
                        break;
                    case R.id.bluroff:
                        makeToastMsg("Blur brush off");
                        myPaint.setNormalBrush();
                        break;
                    case R.id.bluron:
                        makeToastMsg("Blur brush on");
                        myPaint.setBlurBrush();
                        break;
                    case R.id.undo:
                        myPaint.undoLastPath();
                        break;
                }

                drawerLayout.closeDrawers();

                return true;
            });
    }

    private void makeToastMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void chooseBrushSize() {
        bubbleSeekBar.setProgress(myPaint.getBrushSize());
        bubbleSeekBar.setVisibility(View.VISIBLE);

        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                myPaint.setBrushSize(progressFloat);
                bubbleSeekBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseBgColor() {
        bgColor = myPaint.getBackgroundColor();

        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                bgColor = color;
                myPaint.setBackgroundColor(color);
            }
        });

        colorPicker.show();
    }

    public void chooseBrushColor() {
        brushColor = myPaint.getBrushColor();

        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, brushColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {}

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                brushColor = color;
                myPaint.setBrushColor(color);
            }
        });

        colorPicker.show();
    }
}
