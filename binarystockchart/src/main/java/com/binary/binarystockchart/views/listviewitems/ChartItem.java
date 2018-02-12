package com.binary.binarystockchart.views.listviewitems;

import android.content.Context;
import android.view.View;

import com.binary.binarystockchart.charts.BaseBinaryChart;
import com.github.mikephil.charting.data.ChartData;

/**
 * Created by morteza on 2/4/2018.
 */

public abstract class ChartItem<T extends BaseBinaryChart> {

    protected T chart;
    protected View view;
    protected String chartName;

    public ChartItem(String chartName) {
        this.setChartName(chartName);
    }

    public abstract int getItemType();

    public abstract View getView(int position, View convertView, Context c);

    public abstract T getChart(Context c);

    public String getChartName() {
        return this.chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

}
