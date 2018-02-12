package com.binary.binarystockchart.views.listviewitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.charts.BinaryCandleStickChart;
import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.enums.ChartTypes;

/**
 * Created by morteza on 2/5/2018.
 */

public class CandleChartItem extends ChartItem<BinaryCandleStickChart>
{
    public CandleChartItem(String chartName) {
        super(chartName);
    }

    @Override
    public int getItemType() {
        return ChartTypes.CANDLE_CHART.ordinal();
    }

    @Override
    public View getView(int position, View convertView, Context c) {
        CandleChartItem.ViewHolder holder = null;

        if (convertView == null) {
            holder = new CandleChartItem.ViewHolder();

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
            holder = (CandleChartItem.ViewHolder) convertView.getTag();
        }

        this.chart = holder.chart;
        return convertView;
    }

    @Override
    public BinaryCandleStickChart getChart(Context c) {
        return null;
    }

    private static class ViewHolder {
        BinaryCandleStickChart chart;
    }
}
