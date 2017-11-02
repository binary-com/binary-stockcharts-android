package com.binary.binarystockchart.formatter;


import com.binary.binarystockchart.charts.BinaryCandleStickChart;
import com.binary.binarystockchart.charts.BinaryLineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by morteza on 10/10/2017.
 */

public class DateTimeAxisValueFormatter <T> implements IAxisValueFormatter {
    private SimpleDateFormat dateFormat;
    private Date date;
    private T chart;

    public DateTimeAxisValueFormatter(T chart, String format ) {
        super();
        this.chart = chart;
        this.dateFormat = new SimpleDateFormat(format);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.date = new Date();
    }

    public DateTimeAxisValueFormatter(T chart) {
        this(chart, "hh:mm:ss");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Long epochReference = 0L;
        Integer granularity = 1;

        // FIXME Refactor the code to support all type of future charts e.g <T extends DataProvider>
        if ( chart instanceof BinaryLineChart) {
            epochReference = ((BinaryLineChart) chart).getEpochReference();
        } else if (chart instanceof BinaryCandleStickChart) {
            epochReference = ((BinaryCandleStickChart) chart).getEpochReference();
            granularity = ((BinaryCandleStickChart) chart).getGranularity();
        }
        Long timestamp = epochReference + Float.valueOf(value * granularity).longValue();
        this.date.setTime(timestamp * 1000);
        return this.dateFormat.format(this.date);
    }
}
