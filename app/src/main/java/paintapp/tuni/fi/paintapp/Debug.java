package paintapp.tuni.fi.paintapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Debug {
    private static int DEBUG_LEVEL;
    private static boolean UIdebug = false;

    public static void print(String className, String methodName, String message, int debugLevel, Context context) {
        int duration = Toast.LENGTH_LONG;
        CharSequence text = className + ": " + methodName + ", " + message;

        if (UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == debugLevel) {
            Toast.makeText(context, text, duration).show();
        } else if (UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == 5) {
            Toast.makeText(context, text, duration).show();
        }

        if (!UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == debugLevel) {
            Log.d(className, methodName + ", " + message);
        } else if (!UIdebug && BuildConfig.DEBUG && DEBUG_LEVEL == 5) {
            Log.d(className, methodName + ", " + message);
        }
    }

    public static void loadDebug(Context host) {
        DEBUG_LEVEL = host.getResources().getInteger(R.integer.debugLevel);
    }
}