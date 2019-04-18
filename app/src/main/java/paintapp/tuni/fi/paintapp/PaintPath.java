package paintapp.tuni.fi.paintapp;

import android.graphics.Path;

public class PaintPath {

    public float brushSize;
    public int brushColor;
    public int backgroundColor;
    public boolean blur;
    public Path path;

    public PaintPath(float brushSize, int brushColor, int backgroundColor, boolean blur, Path path) {
        this.brushSize = brushSize;
        this.brushColor = brushColor;
        this.backgroundColor = backgroundColor;
        this.blur = blur;
        this.path = path;
    }
}
