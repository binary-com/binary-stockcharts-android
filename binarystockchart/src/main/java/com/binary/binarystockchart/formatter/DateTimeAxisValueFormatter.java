package com.binary.binarystockchart.formatter;


import com.binary.binarystockchart.charts.BinaryLineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by morteza on 10/10/2017.
 */

public class DateTimeAxisValueFormatter implements IAxisValueFormatter {
    private SimpleDateFormat dateFormat;
    private Date date;
    private BinaryLineChart chart;

    public DateTimeAxisValueFormatter(BinaryLineChart chart) {
        super();
        this.chart = chart;
        this.dateFormat = new SimpleDateFormat("hh:mm:ss");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.date = new Date();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Long timestamp = this.chart.getEpochReference() + Float.valueOf(value).longValue();
        this.date.setTime(timestamp * 1000);
        return this.dateFormat.format(this.date);
    }
}
