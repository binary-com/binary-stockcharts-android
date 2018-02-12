package com.binary.binarystockchartsandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.binary.binarystockchart.Indecators.SimpleMovingAverageIndicator;
import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.data.TickEntry;
import com.binary.binarystockchart.enums.ChartTypes;
import com.binary.binarystockchart.views.BinaryChartsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.AssetManager;

public class ChartListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_list_view);

        BinaryChartsView chartView = (BinaryChartsView) findViewById(R.id.chartView);

        BinaryLineChart chart = (BinaryLineChart) chartView.addChart("MAIN", ChartTypes.LINE_CHART);
        BinaryLineChart chart2 = (BinaryLineChart) chartView.addChart("SMA", ChartTypes.LINE_CHART);

        chartView.addEntries(getTickHistory());

        chart.addIndicator(new SimpleMovingAverageIndicator("SMA"));

    }

    private List<TickEntry> getMockData(String fileName) throws IOException {

        boolean isStream = fileName.contains("subscribe");
        List<TickEntry> result = new ArrayList<>();

        String jsonData = AssetManager.readFromAssets(this, fileName);

        try {
            if (!isStream) {
                JSONObject history = new JSONObject(jsonData);
                JSONArray times = history.getJSONArray("times");
                JSONArray prices = history.getJSONArray("prices");

                for (int i = 0; i < times.length(); i++) {
                    TickEntry tick = new TickEntry(
                            Long.valueOf(times.getString(i)),
                            Float.valueOf(prices.getString(i))
                    );
                    result.add(tick);
                }
            } else {
                JSONArray ticks = new JSONArray(jsonData);

                for(int i = 0; i < ticks.length(); i++) {
                    JSONObject item = ticks.getJSONObject(i);
                    TickEntry tick = new TickEntry(
                            Long.valueOf(item.getString("epoch")),
                            Float.valueOf(item.getString("quote"))
                    );
                    result.add(tick);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private List<TickEntry> getTickHistory() {
        try {
            return this.getMockData("line-data.json");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
