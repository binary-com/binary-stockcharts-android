package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.components.CandleMarkerView;
import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.formatter.DateTimeAxisFormatter;
import com.binary.binarystockchart.formatter.DecimalPointAxisFormatter;
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
    private Integer decimalPlaces = 2;
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
        CandleMarkerView mv = new CandleMarkerView(this.getContext(), R.layout.candle_mark_view);
        mv.setChartView(this);
        this.setMarker(mv);
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

    public void addBarrierLine(final Float barrierValue, final String label) {
        LimitLine barrierLine = new LimitLine(barrierValue, label);
        barrierLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        barrierLine.enableDashedLine(30f, 10f, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            barrierLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorBarrierLine));
            barrierLine.setTextColor(ColorUtils.getColor(getContext(), R.color.colorBarrierText));
        } else {
            barrierLine.setLineColor(getResources().getColor(R.color.colorBarrierLine));
            barrierLine.setTextColor(getResources().getColor(R.color.colorBarrierText));
        }

        this.barrierLines.add(barrierLine);
        this.getAxisLeft().addLimitLine(barrierLine);
        this.invalidate();
    }

    public void addBarrierLine(final Float barrierValue) {
        this.addBarrierLine(barrierValue,
                String.format(
                        getContext().getString(R.string.barrier),
                        barrierValue.toString()
                )
        );
    }

    public void removeAllBarriers() {
        for (LimitLine limitLine : this.barrierLines) {
            this.getAxisLeft().removeLimitLine(limitLine);
        }
        this.invalidate();
    }

    public void addStartSpot(BinaryCandleEntry entry) {
        this.startSpotLine = new LimitLine(
                entry.getCandleEntry(this.epochReference, this.granularity).getX());

        this.startSpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorStartSpotLine));
        this.startSpotLine.setLineWidth(2f);
        this.getXAxis().removeAllLimitLines();
        this.entrySpotLine = null;
        this.exitSpotLine = null;
        this.getXAxis().addLimitLine(this.startSpotLine);
        this.invalidate();
    }

    public void addEntrySpot(BinaryCandleEntry entry) {
        this.entrySpotLine = new LimitLine(
                entry.getCandleEntry(this.epochReference, this.granularity).getX()
        );
        this.entrySpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorEntrySpotLit));
        this.exitSpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.entrySpotLine);
        this.invalidate();
    }

    public void addExitSpot(BinaryCandleEntry entry) {
        this.exitSpotLine = new LimitLine(
                entry.getCandleEntry(this.epochReference, this.granularity).getX()
        );
        this.exitSpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorEntrySpotLit));
        this.exitSpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.exitSpotLine);
        this.invalidate();
    }

    private void configXAxis() {
        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateTimeAxisFormatter(this, "hh:mm"));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
    }

    private void configYAxis() {
        YAxis yAxis = this.getAxisRight();
        yAxis.setEnabled(false);

        yAxis = this.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setValueFormatter(new DecimalPointAxisFormatter(4));
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
        this.invalidate();
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

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
}
