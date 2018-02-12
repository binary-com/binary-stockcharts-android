package com.binary.binarystockchart.data;

/**
 * Created by morteza on 2/12/2018.
 */

public class ChartTranslationData {

    private float[] viewPortMatrixValues;
    private int hash;

    public ChartTranslationData(float[] viewPortMatrixValues, int hash) {
        this.viewPortMatrixValues = viewPortMatrixValues;
        this.hash = hash;
    }

    public int getHash() {
        return hash;
    }

    public float[] getViewPortMatrixValues() {
        return viewPortMatrixValues;
    }
}
