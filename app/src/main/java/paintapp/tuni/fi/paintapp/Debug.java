package paintapp.tuni.fi.paintapp;

import android.content.Context;
import android.widget.Toast;

/**
 * Class to help debug the application.
 *
 * @author  Juho Taakala (juho.taakala@tuni.fi)
 * @version 20190422
 * @since   1.8
 */
public class Debug {

    /**
     * Defines debug level.
     */
    private static int DEBUG_LEVEL;

    /**
     * Defines if UI debug is user or not.
     */
    private static boolean UIdebug = false;

    /**
     * Prints debug information.
     *
     * @param className Class name of debugged class.
     * @param methodName Method name of debugged class.
     * @param message Message of debugged class.
     * @param debugLevel Level of debug.
     * @param context Context of the debugged class.
     */
    public static void print(String className, String methodName, String message, int debugLevel, Context context) {
        int duration = Toast.LENGTH_LONG;
        CharSequence text = className + ": " + methodName + ", " + message;

        if (UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == debugLevel) {
            Toast.makeText(context, text, duration).show();
        } else if (UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == 5) {
            Toast.makeText(context, text, duration).show();
        }

        if (!UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == debugLevel) {
            //Log.d(className, methodName + ", " + message);
        } else if (!UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == 5) {
            //Log.d(className, methodName + ", " + message);
        }
    }

    /**
     * Loads debug and gets debug level from debug resources file.
     *
     * @param host Context of the debugged class.
     */
    public static void loadDebug(Context host) {
        DEBUG_LEVEL = host.getResources().getInteger(R.integer.debugLevel);
    }
}