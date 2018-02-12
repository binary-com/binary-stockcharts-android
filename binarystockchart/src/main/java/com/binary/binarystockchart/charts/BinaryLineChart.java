package com.binary.binarystockchart.charts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.data.TickEntry;
import com.binary.binarystockchart.interfaces.indecators.IIndicator;
import com.binary.binarystockchart.utils.ColorUtils;
import com.github.mikephil.charting.components.HighlightArea;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.collect.Iterables;

import java.util.List;

/**
 * Created by morteza on 12/18/2017.
 */

public class BinaryLineChart extends BaseBinaryChart<LineData, TickEntry> {



    public BinaryLineChart(Context context) {
        super(context);
    }

    public BinaryLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BinaryLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    protected void setXAxisMax(float x) {
        mXAxis.setAxisMaximum(x + this.getVisibleXRange() / 3);
    }

    protected LineData generateMainData() {
        CombinedData combinedData = this.generateCombinedData();

        LineData lineData = combinedData.getLineData();

        if(lineData == null) {
            lineData = new LineData();
            combinedData.setData(lineData);
        }

        ILineDataSet lineDataSet = lineData.getDataSetByLabel(
                DataSetLabels.MAIN.toString(),
                false
        );

        if(lineDataSet == null) {
            lineDataSet = createSet();
            lineData.addDataSet(lineDataSet);

            this.setData(combinedData);
        }

        return lineData;
    }

    private ILineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, DataSetLabels.MAIN.toString());

        set.setColor(ColorUtils.getColor(getContext(), R.color.colorLineChart));
        set.setCircleColor(ColorUtils.getColor(getContext(), R.color.colorLineChartCircle));
        set.setCircleColorHole(ColorUtils.getColor(getContext(), R.color.colorLineChartCircle));
        set.setHighLightColor(ColorUtils.getColor(getContext(), R.color.colorCrossHair));
        set.setValueTextColor(ColorUtils.getColor(getContext(), R.color.colorLineChartValue));
        set.setFillColor(ColorUtils.getColor(getContext(), R.color.colorLineChartFill));

        set.setDrawCircles(this.drawCircle);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = getResources().getDrawable(R.drawable.fade_blue);
            set.setFillDrawable(drawable);
        } else {
            set.setFillColor(Color.BLACK);
        }

        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setHighlightEnabled(true);
        set.setValueTextSize(8f);
        set.setDrawValues(false);
        set.setDrawFilled(true);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        return set;
    }

    @Override
    public void addEntry(TickEntry tick) {

        LineData lineData = generateMainData();


        if(this.epochReference == 0L) {
            this.epochReference = tick.getEpoch();
        }

        lineData.addEntry(
                new Entry(tick.getEpoch() - this.epochReference, tick.getQuote()),
                0
        );

        this.handlesIndicators();
        this.getCombinedData().notifyDataChanged();
        this.setXAxisMax(tick.getEpoch() - this.epochReference);

        if (this.plotLineEnabled) {
            this.updatePlotLine(tick.getQuote());
        }

        if (this.autoScrollingEnabled) {
            this.moveViewToX(tick.getEpoch() - this.epochReference);
        }
    }

    @Override
    public void addEntries(List<TickEntry> ticks) {

        LineData lineData = generateMainData();

        if (this.epochReference == 0L) {
            this.epochReference = ticks.get(0).getEpoch();
        }

        for (TickEntry tick : ticks) {
            lineData.addEntry(new Entry(tick.getEpoch() - this.epochReference, tick.getQuote()),
                    "MAIN");
        }

        this.getCombinedData().notifyDataChanged();

        this.handlesIndicators();
        this.notifyDataSetChanged();


        TickEntry lastTick = Iterables.getLast(ticks);
        if (this.plotLineEnabled) {
            this.updatePlotLine(lastTick.getQuote());
        }

        this.setXAxisMax(lastTick.getEpoch() - this.epochReference);

        this.zoom(this.defaultXAxisZoom, defaultYAxisZoom, 0, 0);

        if (this.autoScrollingEnabled) {
            this.moveViewToX(Iterables.getLast(ticks).getEpoch() - this.epochReference);
        }
    }

    @Override
    protected void handlesIndicators() {
        LineData chartData = this.getLineData();

        if (chartData != null && chartData.getEntryCount() > 0) {

            for (IIndicator indicator : this.indicators) {
                if (indicator.getChartData() == null) {
                    indicator.setChartData(this.getCombinedData());
                }

                indicator.notifyDataChanged();
            }
        }
    }

    public void addEntrySpot(Long epoch) {
        this.entrySpotLine = new LimitLine(epoch - this.epochReference);
        this.entrySpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorEntrySpotLit));
        this.entrySpotLine.setLineWidth(2f);
        this.getXAxis().addLimitLine(this.entrySpotLine);
        this.invalidate();
    }

    public void addStartSpot(Long epoch) {
        this.startSpotLine = new LimitLine(epoch - this.epochReference);
        this.startSpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorStartSpotLine));
        this.startSpotLine.setLineWidth(2f);
        this.getXAxis().removeAllLimitLines();
        this.getXAxis().removeHighlightArea(purchaseHighlightArea);
        this.entrySpotLine = null;
        this.exitSpotLine = null;
        this.getXAxis().addLimitLine(this.startSpotLine);
        this.invalidate();
    }

    public void addExitSpot(Long epoch) {
        this.exitSpotLine = new LimitLine(epoch - this.epochReference);
        this.exitSpotLine.setLineWidth(2f);
        this.exitSpotLine.setLineColor(ColorUtils.getColor(getContext(), R.color.colorExitSpotLit));
        this.getXAxis().addLimitLine(this.exitSpotLine);
        this.invalidate();
    }

    public void addHighlightArea(Long epoch, int areaColor) {
        float endPoint = epoch - this.epochReference;

        if (this.exitSpotLine != null) {
            endPoint = exitSpotLine.getLimit();
        }

        if (this.purchaseHighlightArea != null) {
            this.getXAxis().removeHighlightArea(this.purchaseHighlightArea);
        }
        this.purchaseHighlightArea = new HighlightArea(this.entrySpotLine.getLimit(), endPoint);
        this.purchaseHighlightArea.setAreaColor(areaColor);
        this.getXAxis().addHighlightArea(this.purchaseHighlightArea);
        this.invalidate();

    }



}
