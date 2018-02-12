package com.binary.binarystockchart.views.listviewitems;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.charts.BaseBinaryChart;
import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.enums.ChartTypes;
import com.github.mikephil.charting.charts.LineChart;

/**
 * Created by morteza on 2/5/2018.
 */

public class LineChartItem extends ChartItem<BinaryLineChart> {

    public LineChartItem(String chartName) {
        super(chartName);
    }

    @Override
    public int getItemType() {
        return ChartTypes.LINE_CHART.ordinal();
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            if (this.view == null) {
                convertView = LayoutInflater.from(c).inflate(
                        R.layout.list_item_linechart, null);
                this.view = convertView;
            } else {
                convertView = this.view;
            }
            if (this.chart == null) {
                holder.chart = convertView.findViewById(R.id.chart);
            } else {
                holder.chart = this.chart;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        this.chart = holder.chart;
        return convertView;
    }

    @Override
    public BinaryLineChart getChart(Context c) {

        if (this.chart == null) {
            if (this.view == null) {
                this.view = LayoutInflater.from(c).inflate(
                        R.layout.list_item_linechart, null);
            }
            this.chart = this.view.findViewById(R.id.chart);
        }

        return this.chart;
    }

    private static class ViewHolder {
        BinaryLineChart chart;
    }
}
