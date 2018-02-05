package com.binary.binarystockchart.utils;

/**
 * Created by morteza on 11/12/2017.
 */

public class ChartUtils {

    public static float convertEpochToChartX(Long epoch, Long epochReference, Integer granularity) {
        return (epoch - epochReference) / granularity;
    }
}
