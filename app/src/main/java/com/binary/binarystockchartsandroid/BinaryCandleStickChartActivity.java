package com.binary.binarystockchartsandroid;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.binary.binarystockchart.charts.BinaryCandleStickChart;
import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.utils.ChartUtils;
import com.binary.binarystockchart.utils.ColorUtils;
import com.google.common.collect.Iterables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.AssetManager;

public class BinaryCandleStickChartActivity extends AppCompatActivity {

    BinaryCandleStickChart chart;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_candle_stick_chart);

        this.chart = (BinaryCandleStickChart) findViewById(R.id.binaryCandleStickChart);
        this.chart.setDrawGridBackground(false);
        // scaling can now only be done on x- and y-axis separately
        this.chart.setPinchZoom(false);
        this.chart.setAutoScaleMinMaxEnabled(true);
        this.chart.setGranularity(120);

        try {
            List<BinaryCandleEntry> entries = createMockData();
            this.chart.addEntries(entries);
            BinaryCandleEntry entrySpot = Iterables.get(entries, entries.size() - 2);
            this.chart.addStartSpot(entrySpot.getEpoch() - 10);
            this.chart.addEntrySpot(entrySpot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<BinaryCandleEntry> streams = createMockData("candle-data-subscribe-120.json");
                    for (BinaryCandleEntry entry : streams) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chart.addEntry(entry);
                                chart.addHighlightArea(
                                        entry,
                                        streams.indexOf(entry) % 2 == 0 ?
                                                ColorUtils.getColor(chart.getContext(), R.color.colorHighlightAreaWin)
                                                : ColorUtils.getColor(chart.getContext(), R.color.colorHighlightAreaLose));

                                if (streams.indexOf(entry) == 2) {
                                    chart.addBarrierLine(entry.getClose());
                                } else if (streams.indexOf(entry) == 5) {
                                    chart.removeAllBarriers();
                                    chart.addBarrierLine(
                                            entry.getClose(),
                                            String.format("Low Barrier(%s)", entry.getClose().toString())
                                    );
                                    chart.addBarrierLine(
                                            entry.getClose() + 0.500f,
                                            String.format("High Barrier(%s)", String.valueOf(entry.getClose() + 0.500F))
                                    );
                                }
                            }
                        });

                        try{
                            thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chart.getAxisLeft().removeAllLimitLines();
                            chart.invalidate();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private List<BinaryCandleEntry> createMockData() throws IOException {
        return createMockData("candle-data-120.json");
    }

    private List<BinaryCandleEntry> createMockData(String fileName) throws IOException {
        List<BinaryCandleEntry> entries = new ArrayList<>();

        boolean isStream = false;

        if (fileName.equals("candle-data-subscribe-120.json")) {
            isStream = true;
        }

        String jsonData = AssetManager.readFromAssets(this, fileName);

        try {
            JSONObject candleJObject = new JSONObject(jsonData);

            JSONArray entriesJArray = candleJObject.getJSONArray("candles");

            for (int i = 0; i < entriesJArray.length(); i++) {
                JSONObject entry = entriesJArray.getJSONObject(i);

                BinaryCandleEntry candleEntry = new BinaryCandleEntry(
                        Long.valueOf(entry.getString(isStream ? "open_time" : "epoch")),
                        Float.valueOf(entry.getString("high")),
                        Float.valueOf(entry.getString("low")),
                        Float.valueOf(entry.getString("open")),
                        Float.valueOf(entry.getString("close"))
                );

                entries.add(candleEntry);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entries;
    }
}
