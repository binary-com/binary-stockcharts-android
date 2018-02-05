package com.binary.binarystockchart.Indecators;

import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.interfaces.indecators.IIndicator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

/**
 * Created by morteza on 1/7/2018.
 */

public abstract class BaseIndicator implements IIndicator {

    protected String name;
    protected String description;
    protected CombinedData chartData;

    protected BaseIndicator() {
    }

    protected BaseIndicator(String name) {
        this.name = name;
    }

    protected BaseIndicator(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns name of the indicator
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the Indicator
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns description of indicator
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of the indicator
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns relative dataChart
     * @return
     */
    public CombinedData getChartData() {
        return chartData;
    }

    /**
     * Sets dataChart of the indicator
     * @param chartData
     */
    @Override
    public void setChartData(CombinedData chartData) {
        this.chartData = chartData;
    }

    public LineData getLineData() {
        LineData lineData = this.chartData.getLineData();
        if (lineData == null) {
            lineData = new LineData();
            this.chartData.setData(lineData);
            this.chartData.notifyDataChanged();
        }
        return lineData;
    }

    public CandleData getCandleData() {
        CandleData candleData = this.chartData.getCandleData();
        if (candleData == null) {
            candleData = new CandleData();
            this.chartData.setData(candleData);
        }
        return candleData;
    }

    public boolean  isLineChart() {
        if (this.getChartData()
                .getDataSetByLabel(
                        BinaryLineChart.DataSetLabels.MAIN.toString(),
                        false) instanceof ILineDataSet) {
            return true;
        }
        return false;
    }
}
