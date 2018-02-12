package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.components.CandleMarkerView;
import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.formatter.DateTimeAxisFormatter;
import com.binary.binarystockchart.formatter.DecimalPointAxisFormatter;
import com.binary.binarystockchart.interfaces.indecators.IIndicator;
import com.binary.binarystockchart.utils.ChartUtils;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.components.HighlightArea;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.renderer.CombinedChartRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morteza on 10/25/2017.
 */

public class BinaryCandleStickChart extends BaseBinaryChart<CandleData, BinaryCandleEntry> implements OnChartGestureListener {

    private Integer granularity = 60;
    private Integer decimalPlaces = 2;

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

        CandleMarkerView mv = new CandleMarkerView(this.getContext(), R.layout.candle_mark_view);
        mv.setChartView(this);
        this.setMarker(mv);

        this.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });
    }

    protected CandleData generateMainData() {
        CombinedData combinedData = this.generateCombinedData();

        CandleData candleData = combinedData.getCandleData();

        if (candleData == null) {
            candleData = new CandleData();
            combinedData.setData(candleData);
        }

        ICandleDataSet candleDataSet = candleData.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false
        );

        if (candleDataSet == null) {
            candleDataSet = createSet();
            candleData.addDataSet(candleDataSet);

            this.setData(combinedData);
        }

        return candleData;
    }

    @Override
    protected void handlesIndicators() {
        for (IIndicator indicator : this.indicators) {
            if (indicator.getChartData() == null) {
                indicator.setChartData(this.getCombinedData());
                this.setData(this.getCombinedData());
            }

            indicator.notifyDataChanged();
            ((CombinedChartRenderer) this.getRenderer()).createRenderers();
        }
    }

    @Override
    public void addEntry(BinaryCandleEntry entry) {
        CandleData data = this.generateMainData();

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

        if (this.autoScrollingEnabled) {
            this.moveViewToX(entry.getEpoch() - this.epochReference);
        }
    }

    @Override
    public void addEntries(List<BinaryCandleEntry> entries) {
        CandleData data = generateMainData();

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

        if (this.autoScrollingEnabled) {
            this.moveViewToX(entries.get(entries.size() - 1).getEpoch() - this.epochReference);
        }
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

    public void addEntrySpot(Long epoch) {
        this.entrySpotLine = new LimitLine(
                ChartUtils.convertEpochToChartX(epoch, this.epochReference, this.granularity)
        );
        this.entrySpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorEntrySpotLit));
        this.entrySpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.entrySpotLine);
        this.invalidate();
    }

    public void addExitSpot(Long epoch) {
        this.exitSpotLine = new LimitLine(
                ChartUtils.convertEpochToChartX(epoch, this.epochReference, this.granularity)
        );
        this.exitSpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorEntrySpotLit));
        this.exitSpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.exitSpotLine);
        this.invalidate();
    }

    public void addHighlightArea(Long epoch, int areaColor) {
        if (this.entrySpotLine == null) {
            return;
        }

        Float endPoint = ChartUtils.convertEpochToChartX(epoch, this.epochReference, this.granularity);

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

    protected void configXAxis() {
        super.configXAxis();
        this.getXAxis().setValueFormatter(new DateTimeAxisFormatter(this, "hh:mm"));
    }

    protected void setXAxisMax(float x) {
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
