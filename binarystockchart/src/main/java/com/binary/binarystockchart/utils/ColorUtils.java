package com.binary.binarystockchart.utils;

import android.content.Context;
import android.os.Build;

/**
 * Created by morteza on 11/1/2017.
 */

public class ColorUtils {

    public static Integer getColor(Context contex, Integer colorResourceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return contex.getColor(colorResourceId);
        } else {
            return contex.getResources().getColor(colorResourceId);
        }
    }
}
