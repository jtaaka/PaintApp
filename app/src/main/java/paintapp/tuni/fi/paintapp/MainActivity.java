package paintapp.tuni.fi.paintapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends MyBaseActivity {

    private MyPaint myPaint;
    private int bgColor = Color.BLACK;
    private int brushColor = Color.BLACK;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        myPaint = findViewById(R.id.paint);
        myPaint.initialize(width, height);

        mDrawerLayout = findViewById(R.id.drawer_layout);
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
                }

                mDrawerLayout.closeDrawers();

                return true;
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
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
