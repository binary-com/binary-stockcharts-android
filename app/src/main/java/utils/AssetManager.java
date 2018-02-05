package utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by morteza on 10/28/2017.
 */

public class AssetManager {

    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}
