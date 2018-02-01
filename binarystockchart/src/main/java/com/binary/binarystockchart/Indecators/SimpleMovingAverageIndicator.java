package com.binary.binarystockchart.Indecators;

import android.graphics.Color;

import com.binary.binarystockchart.charts.BinaryLineChart;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

/**
 * The Simple Moving Average (SMA) is calculated by adding the price of an instrument over a number of time periods
 * and then dividing the sum by the number of time periods.
 * The SMA is basically the average price of the given time period, with equal weighting given to the price of each period.
 *
 * Created by morteza on 1/7/2018.
 */

public class SimpleMovingAverageIndicator extends BaseIndicator {

    private final String DATA_SET_NAME;

    private Integer period = 21;
    private float strokeWidth = 1.0f;
    private int lastCalculatedEntryIndex = 0;
    private int strokeColor = Color.rgb(83, 182, 173);
    private LineStyles lineStyle = LineStyles.SOLID;
    private EntryPriceParts appliedTo = EntryPriceParts.CLOSE;

    public enum LineStyles {
        SOLID,
        DASH
    }

    public enum EntryPriceParts {
        OPEN,
        HIGH,
        LOW,
        CLOSE
    }

    {
        DATA_SET_NAME = "SMA";
    }

    public SimpleMovingAverageIndicator() {
        super();
    }

    public SimpleMovingAverageIndicator(String name) {
        super(name);
    }

    public SimpleMovingAverageIndicator(String name, String description) {
        super(name, description);
    }

    @Override
    public void notifyDataChanged() {
        if(this.chartData == null) {
            return;
        }

        IDataSet mainDataSet = this.chartData.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false
        );

        if(this.getLineData().getDataSetByLabel(DATA_SET_NAME, false) == null) {
            ILineDataSet dataSet = this.generateDataSet();
            this.getLineData().addDataSet(dataSet);
            this.getLineData().notifyDataChanged();
            this.calSMA(0, this.chartData.getEntryCount());
        } else {
            this.calSMA(this.lastCalculatedEntryIndex, mainDataSet.getEntryCount());
        }
    }

    /**
     * Calculates Simple Moving Average
     *
     * @param from
     * @param to
     */
    private void calSMA(int from, int to) {
        IDataSet mainDataSet = this.chartData.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false
        );
        ILineDataSet smaDataSet = this.getLineData().getDataSetByLabel(DATA_SET_NAME, false);

        if (from == 0) {
            from = this.period;
        }

        for (;from <= to; from ++) {
            float sum = 0.0f;
            float x = mainDataSet.getEntryForIndex(from - 1).getX();
            for (int i = from - this.period; i < from; i++) {
                float y = 0f;

                if (isLineChart()) {
                    y = mainDataSet.getEntryForIndex(i).getY();
                } else {
                    y = ((CandleEntry) (mainDataSet.getEntryForIndex(i))).getClose();
                }

                sum += y;
            }

            if (smaDataSet.getEntryCount() == to - period + 1) {
                smaDataSet.removeLast();
            }

            smaDataSet.addEntry(new Entry(x, sum / this.period));
        }
        this.chartData.notifyDataChanged();
        this.lastCalculatedEntryIndex = to;
    }

    /**
     * Generates required dataSet in dataChart
     *
     * @return
     */
    public ILineDataSet generateDataSet() {
        IDataSet mainDataSet = this.chartData.getDataSetByLabel(
                BinaryLineChart.DataSetLabels.MAIN.toString(),
                false
        );

        LineDataSet dataSet = new LineDataSet(null, DATA_SET_NAME);

        dataSet.setColor(this.strokeColor);
        dataSet.setFillColor(this.strokeColor);
        dataSet.setLineWidth(this.strokeWidth);
        dataSet.setCircleColor(this.strokeColor);
        dataSet.setDrawFilled(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        dataSet.setAxisDependency(mainDataSet.getAxisDependency());

        return dataSet;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public LineStyles getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyles lineStyle) {
        this.lineStyle = lineStyle;
    }

    public EntryPriceParts getAppliedTo() {
        return appliedTo;
    }

    public void setAppliedTo(EntryPriceParts appliedTo) {
        this.appliedTo = appliedTo;
    }

    @Override
    public void destroy() {
        this.chartData.removeDataSet(
                this.chartData.getDataSetByLabel(DATA_SET_NAME, false)
        );

        this.chartData.notifyDataChanged();

    }
}
