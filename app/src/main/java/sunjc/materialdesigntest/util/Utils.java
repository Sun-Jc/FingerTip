package sunjc.materialdesigntest.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SunJc on Feb/18/16.
 */
public class Utils {
    // bitmap loader
    static public Bitmap loadBitmap(AssetManager loader, String file) {
        Bitmap result = null;
        InputStream bitmapStream=null;
        try {
            //open the file from the assets folder with the given name
            bitmapStream = loader.open(file);

            //decode the stream as a bitmap
            result = BitmapFactory.decodeStream(bitmapStream);
            //set up an inputStream
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bitmapStream!=null)
                try {
                    //close the inputstream if it was loaded successfully
                    bitmapStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("sunjcdebug","load failed");
                }
        }
        return result;
    }

    public static String convertToHumanReadableDate(String timestamp) {
        SimpleDateFormat fmtOut = new SimpleDateFormat();
        return fmtOut.format(new Date(Long.valueOf(timestamp)));
    }

}
