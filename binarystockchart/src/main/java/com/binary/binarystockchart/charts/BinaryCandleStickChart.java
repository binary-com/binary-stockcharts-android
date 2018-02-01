package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.components.CandleMarkerView;
import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.formatter.DateTimeAxisFormatter;
import com.binary.binarystockchart.formatter.DecimalPointAxisFormatter;
import com.binary.binarystockchart.interfaces.indecators.IIndicator;
import com.binary.binarystockchart.utils.ChartUtils;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.HighlightArea;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morteza on 10/25/2017.
 */

public class BinaryCandleStickChart extends CombinedChart implements OnChartGestureListener {
    private Long epochReference = 0L;
    private Integer granularity = 60;
    private Integer decimalPlaces = 2;
    private Boolean plotLineEnabled = true;

    private LimitLine plotLine;
    private LimitLine startSpotLine;
    private LimitLine entrySpotLine;
    private LimitLine exitSpotLine;
    private List<LimitLine> barrierLines = new ArrayList<>();
    private HighlightArea purchaseHighlightArea;
    private Boolean autoScrollingEnabled = true;
    private List<IIndicator> indicators = new ArrayList<>();

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
        this.setOnChartGestureListener(this);
        configXAxis();
        configYAxis();

        this.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });
    }

    private CombinedData generateCombinedData() {
        CombinedData combinedData = this.getData();

        if(combinedData == null) {
            combinedData = new CombinedData();
        }
        return combinedData;
    }

    private CandleData generateCandleData() {
        CombinedData combinedData = this.generateCombinedData();

        CandleData candleData = combinedData.getCandleData();

        if(candleData == null) {
            candleData = new CandleData();
            combinedData.setData(candleData);
        }

        ICandleDataSet candleDataSet = candleData.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false
        );

        if(candleDataSet == null) {
            candleDataSet = createSet();
            candleData.addDataSet(candleDataSet);

            this.setData(combinedData);
        }

        return candleData;
    }

    private void handlesIndicators() {
        for(IIndicator indicator : this.indicators) {
            if(indicator.getChartData() == null) {
                indicator.setChartData(this.getCombinedData());
                this.setData(this.getCombinedData());
            }

            indicator.notifyDataChanged();
            ((CombinedChartRenderer) this.getRenderer()).createRenderers();
        }
    }

    public void addEntry(BinaryCandleEntry entry) {
        CandleData data = this.generateCandleData();

        if (this.epochReference == 0L) {
            this.epochReference = entry.getEpoch();
        }

        ICandleDataSet set = data.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false);
        CandleEntry lastEntry = set.getEntryForIndex(set.getEntryCount() - 1);

        if (lastEntry.getX() == entry.getCandleEntry(this.epochReference, this.granularity).getX()) {
            set.removeLast();
    }

        data.addEntry(entry.getCandleEntry(this.epochReference, this.granularity), "MAIN");
        data.notifyDataChanged();

        this.handlesIndicators();

        this.getCombinedData().notifyDataChanged();

        this.setXAxisMax(entry.getCandleEntry(this.epochReference, this.granularity).getX());

        if (this.plotLineEnabled) {
            this.updatePlotLine(entry.getClose());
        }

        this.notifyDataSetChanged();

        if(this.autoScrollingEnabled) {
            this.moveViewToX(entry.getEpoch() - this.epochReference);
        }
    }

    public void addEntries(List<BinaryCandleEntry> entries) {
        CandleData data = generateCandleData();

        if (this.epochReference == 0L) {
            this.epochReference = entries.get(0).getEpoch();
        }

        for (BinaryCandleEntry entry : entries) {
            data.addEntry(entry.getCandleEntry(this.epochReference, this.granularity), 0);
        }

        this.handlesIndicators();

        this.getCombinedData().notifyDataChanged();

        this.zoom(2, 1, 0, 0);

        this.notifyDataSetChanged();

        if(this.autoScrollingEnabled) {
            this.moveViewToX(entries.get(entries.size() - 1).getEpoch() - this.epochReference);
        }
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

    public void removeAllBarriers() {
        for (LimitLine limitLine : this.barrierLines) {
            this.getAxisLeft().removeLimitLine(limitLine);
        }
        this.invalidate();
    }

    public void addStartSpot(Long epoch) {
        this.startSpotLine = new LimitLine(
                ChartUtils.convertEpochToChartX(epoch, this.epochReference, this.granularity));

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
        this.entrySpotLine.setLineWidth(2f);
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

    public void addHighlightArea(BinaryCandleEntry entry, int areaColor) {
        if (this.entrySpotLine == null) {
            return;
        }

        Float endPoint = entry.getCandleEntry(this.epochReference, this.granularity).getX();

        if (this.exitSpotLine != null) {
            endPoint = this.exitSpotLine.getLimit();
        }

        if (this.purchaseHighlightArea != null) {
            this.getXAxis().removeHighlightArea(this.purchaseHighlightArea);
        }

        this.purchaseHighlightArea = new HighlightArea(this.entrySpotLine.getLimit(), endPoint);
        this.purchaseHighlightArea.setAreaColor(areaColor);
        this.getXAxis().addHighlightArea(this.purchaseHighlightArea);
    }

    private void configXAxis() {
        XAxis xAxis = this.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateTimeAxisFormatter(this, "hh:mm"));
        xAxis.setAxisMinimum(0f);
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

    private void setXAxisMax(float x) {
        mXAxis.setAxisMaximum(x + this.getVisibleXRange() / 2);
    }

    private ICandleDataSet createSet() {
        CandleDataSet set = new CandleDataSet(
                null,
                BinaryLineChart.DataSetLabels.MAIN.toString()
        );
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
        this.plotLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        this.plotLine.enableDashedLine(30f, 10f, 0);

        this.plotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorPlotLine));
        this.plotLine.setTextColor(ColorUtils.getColor(getContext(), R.color.colorPlotText));

        this.plotLine.setLabelBackground(LimitLine.LimitLineLabelBackground.POLYGON);
        this.plotLine.setLabelBackgroundColor(ColorUtils.getColor(getContext(),
                R.color.colorPlotBg));

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

    public void addIndicator(IIndicator indicator) {
        this.indicators.add(indicator);
    }

    public void removeIndicator(IIndicator indicator) {
        this.indicators.remove(indicator);
    }

    public void removeIndicator(int index) {
        this.indicators.remove(index);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        float result = Math.abs(Math.round(this.getHighestVisibleX()) - this.getXAxis().getAxisMaximum());
        this.setXAxisMax(this.getXAxis().getAxisMaximum());
        if( result < 1 && result >= 0) {
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
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
