package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.formatter.DateTimeAxisValueFormatter;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morteza on 10/25/2017.
 */

public class BinaryCandleStickChart extends CandleStickChart {
    private Long epochReference = 0L;
    private Integer granularity = 60;
    private Boolean plotLineEnabled = true;

    private LimitLine plotLine;
    private LimitLine startSpotLine;
    private LimitLine entrySpotLine;
    private LimitLine exitSpotLine;
    private List<LimitLine> barrierLines = new ArrayList<>();

    public BinaryCandleStickChart(Context context) {
        super(context);
    }

    public BinaryCandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BinaryCandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init() {
        super.init();
        this.getDescription().setEnabled(false);
        this.getLegend().setEnabled(false);
        configXAxis();
        configYAxis();
    }


    public void addEntry(BinaryCandleEntry entry) {
        CandleData data = this.getData();

        if (data == null) {
            data = new CandleData();
            this.setData(data);
            this.invalidate();
        }

        ICandleDataSet set = data.getDataSetByIndex(0);

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
            this.epochReference = entry.getEpoch();
        }

        CandleEntry lastEntry = set.getEntryForIndex(set.getEntryCount() - 1);

        if (lastEntry.getX() == entry.getCandleEntry(this.epochReference, this.granularity).getX()) {
            set.removeLast();
            data.notifyDataChanged();
        }

        data.addEntry(entry.getCandleEntry(this.epochReference, this.granularity), 0);
        data.notifyDataChanged();

        if (this.plotLineEnabled) {
            this.updatePlotLine(entry.getClose());
        }

        this.notifyDataSetChanged();
        this.setVisibleXRangeMinimum(3f);
//        this.setVisibleXRangeMaximum(5f);
        this.moveViewToX(entry.getEpoch() - this.epochReference);
    }

    public void addEntries(List<BinaryCandleEntry> entries) {
        CandleData data = this.getData();

        if (data == null) {
            data = new CandleData();
            this.setData(data);
            this.invalidate();
        }

        ICandleDataSet set = data.getDataSetByIndex(0);

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
            this.epochReference = entries.get(0).getEpoch();
        }

        for (BinaryCandleEntry entry : entries) {
            data.addEntry(entry.getCandleEntry(this.epochReference, this.granularity), 0);
        }

        this.notifyDataSetChanged();
        this.setVisibleXRangeMinimum(3f);
//        this.setVisibleXRangeMaximum(5f);
        this.moveViewToX(entries.get(entries.size() - 1).getEpoch() - this.epochReference);
    }

    public void addStartSpot(BinaryCandleEntry entry) {
        this.startSpotLine = new LimitLine(
                entry.getCandleEntry(this.epochReference, this.granularity).getX());
    }

    private void configXAxis() {
        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateTimeAxisValueFormatter(this, "hh:mm"));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
    }

    private void configYAxis() {
        YAxis yAxis = this.getAxisRight();
        yAxis.setEnabled(false);

        yAxis = this.getAxisLeft();
        yAxis.setDrawGridLines(false);
    }

    private ICandleDataSet createSet() {
        CandleDataSet set = new CandleDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);

        set.setShadowColor(ColorUtils.getColor(getContext(), R.color.colorCandleShadow));
        set.setDecreasingColor(ColorUtils.getColor(getContext(), R.color.colorCandleDecreasing));
        set.setIncreasingColor(ColorUtils.getColor(getContext(), R.color.colorCandleIncreasing));
        set.setNeutralColor(ColorUtils.getColor(getContext(), R.color.colorCandleNatural));
        //set.setShadowWidth(0.7f);

        set.setDecreasingPaintStyle(Paint.Style.FILL);

        set.setIncreasingPaintStyle(Paint.Style.FILL);


        set.setHighlightEnabled(true);

        return set;
    }

    private void updatePlotLine(Float value) {
        if (plotLine != null) {
            this.getAxisLeft().removeLimitLine(plotLine);
        }
        this.plotLine = new LimitLine(value, value.toString());
        this.plotLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        this.plotLine.enableDashedLine(30f, 10f, 0);

        this.plotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorPlotLine));
        this.plotLine.setTextColor(ColorUtils.getColor(getContext(), R.color.colorPlotText));

        this.plotLine.setTextColor(Color.rgb(46, 136, 54));
        this.getAxisLeft().addLimitLine(plotLine);
    }

    public Long getEpochReference() {
        return epochReference;
    }

    public void setEpochReference(Long epochReference) {
        this.epochReference = epochReference;
    }

    public Integer getGranularity() {
        return granularity;
    }

    public void setGranularity(Integer granularity) {
        this.granularity = granularity;
    }
}
