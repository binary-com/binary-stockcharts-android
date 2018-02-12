package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.data.ChartTranslationData;
import com.binary.binarystockchart.formatter.DateTimeAxisFormatter;
import com.binary.binarystockchart.interfaces.charts.IBinaryChart;
import com.binary.binarystockchart.interfaces.data.IEntry;
import com.binary.binarystockchart.interfaces.indecators.IIndicator;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.HighlightArea;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineRadarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by morteza on 2/2/2018.
 */

public abstract class BaseBinaryChart <T extends BarLineScatterCandleBubbleData, Y extends IEntry> extends CombinedChart implements IBinaryChart<T>, OnChartGestureListener {

    protected Boolean plotLineEnabled = true;
    protected Boolean autoScrollingEnabled = true;
    protected Boolean drawCircle = false;
    protected Long epochReference = 0L;
    protected Integer defaultXAxisZoom = 20;
    protected Integer defaultYAxisZoom = 1;
    protected LimitLine plotLine;
    protected LimitLine startSpotLine;
    protected LimitLine entrySpotLine;
    protected LimitLine exitSpotLine;
    protected List<LimitLine> barrierLines = new ArrayList<>();
    protected HighlightArea purchaseHighlightArea;
    protected List<IIndicator> indicators = new ArrayList<>();
    protected PublishSubject<ChartTranslationData> viewPortEmitter;

    public enum DataSetLabels {
        MAIN("MAIN");

        private final String value;

        DataSetLabels(String _value) {
            value = _value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public BaseBinaryChart(Context context) {
        super(context);
    }

    public BaseBinaryChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseBinaryChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void init() {
        super.init();
        this.getDescription().setEnabled(false);
        this.getLegend().setEnabled(false);
        this.setOnChartGestureListener(this);
        configYAxis();
        configXAxis();
    }

    protected CombinedData generateCombinedData() {
        CombinedData combinedData = this.getData();

        if(combinedData == null) {
            combinedData = new CombinedData();
        }
        return combinedData;
    }

    public void addBarrierLine(final Float barrierValue, final String label) {
        LimitLine barrierLine = new LimitLine(barrierValue, label);
        barrierLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        barrierLine.enableDashedLine(30f, 10f, 0);
        barrierLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorBarrierLine));
        barrierLine.setTextColor(ColorUtils.getColor(getContext(), R.color.colorBarrierText));

        barrierLine.setLabelBackground(LimitLine.LimitLineLabelBackground.RECTANGLE);
        barrierLine.setLabelBackgroundStyle(Paint.Style.STROKE);
        barrierLine.setLabelBackgroundColor(ColorUtils.getColor(getContext(),
                R.color.colorBarrierBg));

        this.barrierLines.add(barrierLine);
        this.getAxisLeft().addLimitLine(barrierLine);
        this.invalidate();
    }

    public void addBarrierLine(final Float barrierValue) {
        this.addBarrierLine(barrierValue, barrierValue.toString());
    }

    public void removeAllBarrierLines() {
        for (LimitLine limitLine : this.barrierLines) {
            this.getAxisLeft().removeLimitLine(limitLine);
        }
        this.invalidate();
    }

    protected void updatePlotLine(Float value) {
        if (plotLine != null) {
            this.getAxisLeft().removeLimitLine(plotLine);
        }
        this.plotLine = new LimitLine(value, value.toString());
        this.plotLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);

        this.plotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorPlotLine));
        this.plotLine.setTextColor(ColorUtils.getColor(getContext(), R.color.colorPlotText));

        this.plotLine.setLabelBackground(LimitLine.LimitLineLabelBackground.POLYGON);
        this.plotLine.setLabelBackgroundColor(ColorUtils.getColor(getContext(),
                R.color.colorPlotBg));

        this.getAxisLeft().addLimitLine(plotLine);
        this.invalidate();
    }

    protected void configXAxis() {
        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateTimeAxisFormatter(this));
        xAxis.setLabelCount(5);
//        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
    }

    protected void configYAxis() {
        YAxis yAxis = this.getAxisRight();
        yAxis.setEnabled(false);
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

    public Boolean getDrawCircle() {
        return drawCircle;
    }

    public Integer getDefaultXAxisZoom() {
        return defaultXAxisZoom;
    }

    public void setDefaultXAxisZoom(Integer defaultXAxisZoom) {
        this.defaultXAxisZoom = defaultXAxisZoom;
    }

    public Integer getDefaultYAxisZoom() {
        return defaultYAxisZoom;
    }

    public void setDefaultYAxisZoom(Integer defaultYAxisZoom) {
        this.defaultYAxisZoom = defaultYAxisZoom;
    }

    public void setDrawCircle(Boolean drawCircle) {
        this.drawCircle = drawCircle;

        List<ILineRadarDataSet> sets = this.generateMainData().getDataSets();

        for (ILineRadarDataSet iSet : sets) {
            LineDataSet set = (LineDataSet) iSet;
            set.setDrawCircles(this.drawCircle);
        }
        this.invalidate();
    }

    public void addIndicator(IIndicator indicator) {
        this.indicators.add(indicator);
        this.handlesIndicators();
    }

    public void removeIndicator(IIndicator indicator) {
        this.indicators.remove(indicator);
    }

    public void removeIndicator(int index) {
        this.indicators.remove(index);
    }

    public PublishSubject<ChartTranslationData> getViewPortEmitter() {
        return viewPortEmitter;
    }

    public void setViewPortEmitter(PublishSubject<ChartTranslationData> viewPortEmitter) {
        this.viewPortEmitter = viewPortEmitter;

        this.viewPortEmitter.subscribe( o -> {
            if( this.hashCode() != o.getHash()) {
                Matrix matrix = this.getViewPortHandler().getMatrixTouch();
                matrix.setValues(o.getViewPortMatrixValues());
                this.getViewPortHandler().refresh(matrix, this, true);
            }
        });
    }

    protected abstract void setXAxisMax(float x);
    protected abstract T generateMainData();
    protected abstract void handlesIndicators();
    public abstract void addEntry(Y entry);
    public abstract void addEntries(List<Y> entries);


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        float result = Math.abs(Math.round(this.getHighestVisibleX()) - this.getXAxis().getAxisMaximum());
        if (result < 1 && result >= 0) {
            this.autoScrollingEnabled = true;
        } else {
            this.autoScrollingEnabled = false;
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        if ( this.autoScrollingEnabled) {
            this.moveViewToX(this.getHighestVisibleX());
        }

        if (this.viewPortEmitter != null) {
            float[] vals = new float[9];
            Matrix matrix = this.getViewPortHandler().getMatrixTouch();
            matrix.getValues(vals);
            this.viewPortEmitter.onNext(
                    new ChartTranslationData(vals, this.hashCode())
            );
        }
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

        if (this.viewPortEmitter != null) {
            float[] vals = new float[9];
            Matrix matrix = this.getViewPortHandler().getMatrixTouch();
            matrix.getValues(vals);
            this.viewPortEmitter.onNext(
                    new ChartTranslationData(vals, this.hashCode())
            );
        }
    }
}
