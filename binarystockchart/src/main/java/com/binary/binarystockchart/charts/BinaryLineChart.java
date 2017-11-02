package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.data.TickEntry;
import com.binary.binarystockchart.formatter.DateTimeAxisValueFormatter;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.HighlightArea;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morteza on 10/10/2017.
 */

public class BinaryLineChart extends LineChart {

    private Boolean plotLineEnabled = true;
    private Long epochReference = 0L;
    private LimitLine plotLine;
    private LimitLine startSpotLine;
    private LimitLine entrySpotLine;
    private LimitLine exitSpotLine;
    private List<LimitLine> barrierLines = new ArrayList<>();
    private HighlightArea purchaseHighlightArea;

    public BinaryLineChart(Context context) {
        super(context);
    }

    public BinaryLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BinaryLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init() {
        super.init();
        this.getDescription().setEnabled(false);
        this.getLegend().setEnabled(false);
        configYAxis();
        configXAxis();
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void configXAxis() {
        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateTimeAxisValueFormatter(this.epochReference));
        xAxis.setLabelCount(5);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

    private void configYAxis() {
        YAxis yAxis = this.getAxisRight();
        yAxis.setEnabled(false);
    }

    public void addTick(TickEntry tick) {
        LineData data = this.getData();

        if (data == null) {
            data = new LineData();
            this.setData(data);
            this.invalidate();
        }

        ILineDataSet set = data.getDataSetByIndex(0);

        if (set == null) {
            set = createSet();
            data.addDataSet(set);
            this.epochReference = tick.getEpoch();
        }

        data.addEntry(new Entry(tick.getEpoch() - this.epochReference, tick.getQuote()), 0);
        data.notifyDataChanged();

        if (this.plotLineEnabled) {
            this.updatePlotLine(tick.getQuote());
        }

        this.notifyDataSetChanged();
        this.setVisibleXRangeMinimum(5f);
        this.setVisibleXRangeMaximum(15f);
        this.moveViewToX(tick.getEpoch() - this.epochReference);
    }

    public void addEntrySpot(TickEntry tick) {
        this.entrySpotLine = new LimitLine(tick.getEpoch() - this.epochReference);
        this.entrySpotLine.setLineColor(Color.GREEN);
        this.entrySpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.entrySpotLine);
    }

    public void addStartSpot(TickEntry tick) {
        this.startSpotLine = new LimitLine(tick.getEpoch() - this.epochReference);
        this.startSpotLine.setLineColor(Color.GRAY);
        this.startSpotLine.setLineWidth(2f);
        this.getXAxis().removeAllLimitLines();
        this.getXAxis().removeHighlightArea(purchaseHighlightArea);
        this.entrySpotLine = null;
        this.exitSpotLine = null;
        this.getXAxis().addLimitLine(this.startSpotLine);
    }

    public void addExitSpot(TickEntry tick) {
        this.exitSpotLine = new LimitLine(tick.getEpoch() - this.epochReference);
        this.exitSpotLine.setLineWidth(2f);
        this.exitSpotLine.setLineColor(Color.RED);
        this.getXAxis().addLimitLine(this.exitSpotLine);
    }

    public void addHighlightArea(TickEntry tick, int areaColor) {
        float endPoint = tick.getEpoch() - this.epochReference;

        if (this.exitSpotLine != null) {
            endPoint = exitSpotLine.getLimit();
        }

        if (this.purchaseHighlightArea != null) {
            this.getXAxis().removeHighlightArea(this.purchaseHighlightArea);
        }
        this.purchaseHighlightArea = new HighlightArea(this.entrySpotLine.getLimit(), endPoint);
        this.purchaseHighlightArea.setAreaColor(areaColor);
        this.getXAxis().addHighlightArea(this.purchaseHighlightArea);

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

        this.getAxisLeft().addLimitLine(barrierLine);
    }

    public void addBarrierLine(final Float barrierValue) {
        this.addBarrierLine(barrierValue, String.format("Barrier(%s)", barrierValue.toString()));
    }

    private void removeAllBarrierLines() {
        for (LimitLine limitLine : this.barrierLines) {
            this.getAxisLeft().removeLimitLine(limitLine);
        }
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

    private ILineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, null);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        set.setColor(ColorUtils.getColor(getContext(), R.color.colorLineChart));
        set.setCircleColor(ColorUtils.getColor(getContext(), R.color.colorLineChartCircle));
        set.setCircleColorHole(ColorUtils.getColor(getContext(), R.color.colorLineChartCircle));
        set.setHighLightColor(ColorUtils.getColor(getContext(), R.color.colorCrossHair));
        set.setValueTextColor(ColorUtils.getColor(getContext(), R.color.colorLineChartValue));
        set.setFillColor(ColorUtils.getColor(getContext(), R.color.colorLineChartFill));

        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setHighlightEnabled(true);
        set.setValueTextSize(8f);
        set.setDrawValues(false);

        return set;
    }

    public void setDescription(String description) {
        Description desc = new Description();
        desc.setText(description);
        setDescription(desc);
    }

    public Long getEpochReference() {
        return epochReference;
    }

    public void setEpochReference(Long epochReference) {
        this.epochReference = epochReference;
    }

    public Boolean getPlotLineEnabled() {
        return plotLineEnabled;
    }

    public void setPlotLineEnabled(Boolean plotLineEnabled) {
        this.plotLineEnabled = plotLineEnabled;
    }
}

