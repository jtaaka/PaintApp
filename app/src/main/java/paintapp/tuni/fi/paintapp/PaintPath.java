package paintapp.tuni.fi.paintapp;

import android.graphics.Path;

/**
 * Class that stores information of settings and path drawn on canvas.
 *
 * @author  Juho Taakala (juho.taakala@tuni.fi)
 * @version 20190422
 * @since   1.8
 */
public class PaintPath {

    /**
     * Defines brush size.
     */
    public float brushSize;

    /**
     * Defines brush color.
     */
    public int brushColor;

    /**
     * Defines background color.
     */
    public int backgroundColor;

    /**
     * Defines if blur brush is used or not.
     */
    public boolean blur;

    /**
     * Defines a path drawn on canvas.
     */
    public Path path;

    /**
     * Constructs the PaintPath class.
     *
     * @param brushSize Size of a brush.
     * @param brushColor Color or a brush.
     * @param backgroundColor Color of background.
     * @param blur Defines if blur brush is used or not.
     * @param path Path drawn on canvas.
     */
    public PaintPath(float brushSize, int brushColor, int backgroundColor, boolean blur, Path path) {
        this.brushSize = brushSize;
        this.brushColor = brushColor;
        this.backgroundColor = backgroundColor;
        this.blur = blur;
        this.path = path;
    }
}
