package paintapp.tuni.fi.paintapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

/**
 * Main activity class.
 *
 * @author  Juho Taakala (juho.taakala@tuni.fi)
 * @version 20190422
 * @since   1.8
 */
public class MainActivity extends MyBaseActivity {

    /**
     * Defines MyPaint class.
     */
    private MyPaint myPaint;

    /**
     * Default background color for color picker.
     */
    private int bgColor = Color.WHITE;

    /**
     * Default brush color for color picker.
     */
    private int brushColor = Color.BLACK;

    /**
     * Defines if user gives acces to storage or not.
     */
    private boolean accessToStorage;

    /**
     * Defines drawer layout for menu items.
     */
    private DrawerLayout drawerLayout;

    /**
     * Defines a slider for brush size change.
     */
    private BubbleSeekBar brushSizeSlider;

    /**
     * Defines a card view used with brush size slider.
     */
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

    /**
     * Sets up a listener to listen to menu item clicks and does actions based on clicked item.
     *
     * @param navigationView Navigation view for menu items.
     */
    private void setupDrawerContent(@NonNull NavigationView navigationView) {
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

    /**
     * Makes a Toast message.
     *
     * @param message Message to display on screen.
     * @param duration Duration that the message is shown on screen.
     */
    private void makeToastMsg(String message, int duration) {
        Toast.makeText(this, message, duration).show();
    }

    /**
     * Opens up a slider on card view to set the brush size.
     */
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

    /**
     * Opens up a color picker dialog for setting the background color for canvas.
     */
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

    /**
     * Opens up a color picker dialog for setting the brush color.
     */
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

    /**
     * Saves the currently drawn canvas to user's gallery.
     */
    public void saveToGallery() {
        if (accessToStorage) {
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                    myPaint.getImage(),"PaintMasterPiece","Mom, look what I made");

            makeToastMsg("Canvas saved to gallery", Toast.LENGTH_SHORT);
        } else {
            makeToastMsg("You have denied access to gallery", Toast.LENGTH_LONG);
        }
    }


    /**
     * Gets image from user's gallery.
     */
    public void getImageFromGallery() {
        if (accessToStorage) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, 1);
        } else {
            makeToastMsg("You have denied access to gallery", Toast.LENGTH_LONG);
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
            rotateGalleryImage(image, picturePath);
        }
    }

    /**
     * Rotates user's gallery image vertically.
     *
     * @param image Image selected from gallery.
     * @param picturePath Path of the file.
     */
    public void rotateGalleryImage(Bitmap image, String picturePath) {
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }

            Bitmap bitMap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitMap, width, height, true);

            myPaint.setImage(scaledBitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
