package com.binary.binarystockchart.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by morteza on 11/4/2017.
 */

public class DecimalPointAxisFormatter implements IAxisValueFormatter {

    Integer decimalPlaces;

    public DecimalPointAxisFormatter(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String format = "%." + this.decimalPlaces + "f";
        return String.format(format, value);
    }
}
