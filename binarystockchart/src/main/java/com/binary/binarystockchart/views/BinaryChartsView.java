package com.binary.binarystockchart.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.charts.BaseBinaryChart;
import com.binary.binarystockchart.data.ChartTranslationData;
import com.binary.binarystockchart.enums.ChartTypes;
import com.binary.binarystockchart.interfaces.data.IEntry;
import com.binary.binarystockchart.views.listviewitems.CandleChartItem;
import com.binary.binarystockchart.views.listviewitems.ChartItem;
import com.binary.binarystockchart.views.listviewitems.LineChartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by morteza on 2/4/2018.
 */

public class BinaryChartsView extends RelativeLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;
    private View view;
    private ListView chartListView;
    private ArrayList<ChartItem> chartsList = new ArrayList<>();
    private ChartDataAdapter chartDataAdapter;
    private PublishSubject<ChartTranslationData> viewPortEmitter = PublishSubject.create();

    public BinaryChartsView(Context context) {
        super(context);
        this.context = context;
        this.initView();
    }

    public BinaryChartsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        this.initView();
    }

    public BinaryChartsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        this.initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BinaryChartsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        this.initView();
    }

    private void initView() {
        this.view = this;
        inflate(this.context, R.layout.binary_chart_view, this);

        this.chartListView = (ListView) findViewById(R.id.chartsList);

        this.chartDataAdapter = new ChartDataAdapter(this.context, this.chartsList);

        this.chartListView.setAdapter(this.chartDataAdapter);

//        this.viewPortEmitter.subscribe( o -> Log.d("viewPortEmitter", String.valueOf(o)));

    }

    public BaseBinaryChart addChart(String chartName, ChartTypes type) {

        ChartItem<?> chart = null;
        switch (type) {
            case LINE_CHART:
                chart = new LineChartItem(chartName);
                this.chartsList.add(chart);
                this.chartDataAdapter.notifyDataSetChanged();
                break;
            case CANDLE_CHART:
                chart = new CandleChartItem(chartName);
                this.chartsList.add(chart);
                this.chartDataAdapter.notifyDataSetChanged();
                break;
        }
        chart.getChart(this.context).setViewPortEmitter(this.viewPortEmitter);
        return chart.getChart(this.context);
    }

    public void addEntry(IEntry entry) {
        for (ChartItem c : this.chartsList) {
            c.getChart(this.context).addEntry(entry);
        }
    }

    public void addEntries(List<? extends IEntry> entries) {
        for (ChartItem c : this.chartsList) {
            c.getChart(this.context).addEntries(entries);
        }
    }

    public BaseBinaryChart getChart(String chartName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Stream<ChartItem> chartStream = this.chartsList.stream()
                    .filter(o -> o.getChartName().equals(chartName));

            if (chartStream.findFirst().isPresent()) {
                return chartStream.findFirst().get().getChart(this.context);
            }
        } else {
            for (ChartItem c : this.chartsList) {
                if (c.getChartName().equals(chartName)) {
                    return c.getChart(this.context);
                }
            }
        }
        return null;
    }

    public BaseBinaryChart getChart(int index) {
        if (this.chartsList.size() < index) {
            return null;
        }

        return this.chartsList.get(index).getChart(this.context);
    }


    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return ChartTypes.values().length;
        }
    }
}
