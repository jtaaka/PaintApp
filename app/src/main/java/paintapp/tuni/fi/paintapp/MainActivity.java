package paintapp.tuni.fi.paintapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
    private boolean accessToStorage;

    private DrawerLayout drawerLayout;
    private BubbleSeekBar brushSizeSlider;
    private MaterialCardView cardView;

    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        brushSizeSlider = findViewById(R.id.slider);
        brushSizeSlider.setVisibility(View.INVISIBLE);

        cardView = findViewById(R.id.cardView);
        cardView.setVisibility(View.INVISIBLE);

        setSupportActionBar(findViewById(R.id.toolbar));

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        myPaint = findViewById(R.id.paint);
        myPaint.initialize(width, height);

        drawerLayout = findViewById(R.id.drawer_layout);
        setupDrawerContent(findViewById(R.id.navigationView));

        Debug.loadDebug(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessToStorage = true;
            } else {
                accessToStorage = false;
                makeToastMsg("Now you can not save your canvas", Toast.LENGTH_LONG);
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
            menuItem -> {
                switch (menuItem.getItemId()) {
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
                        makeToastMsg("Blur brush off", Toast.LENGTH_SHORT);
                        myPaint.setNormalBrush();
                        break;
                    case R.id.bluron:
                        makeToastMsg("Blur brush on", Toast.LENGTH_SHORT);
                        myPaint.setBlurBrush();
                        break;
                    case R.id.gallery:
                        getImageFromGallery();
                        break;
                    case R.id.save:
                        saveToGallery();
                        break;
                }

                drawerLayout.closeDrawers();

                return true;
            });
    }

    private void makeToastMsg(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    private void chooseBrushSize() {
        brushSizeSlider.setProgress(myPaint.getBrushSize());
        brushSizeSlider.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        brushSizeSlider.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {}

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                myPaint.setBrushSize(progressFloat);
                bubbleSeekBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.reset:
                myPaint.reset();
                break;
            case R.id.undo:
                myPaint.undoLastPath();
                break;
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

    public void saveToGallery() {
        if (accessToStorage) {
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                    myPaint.getImage(),"PaintMasterPiece","Mom, look what I made");

            makeToastMsg("Canvas saved to gallery", Toast.LENGTH_SHORT);
        } else {
            makeToastMsg("You have denied access to gallery", Toast.LENGTH_SHORT);
        }
    }

    public void getImageFromGallery() {
        if (accessToStorage) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap image = BitmapFactory.decodeFile(picturePath);

            myPaint.setImage(image);
        }
    }
}
