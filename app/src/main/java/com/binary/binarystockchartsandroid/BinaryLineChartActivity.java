package com.binary.binarystockchartsandroid;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.data.TickEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.AssetManager;

public class BinaryLineChartActivity extends Activity {

    private BinaryLineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_line_chart);

        this.chart = findViewById(R.id.binaryLineChart);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                chart.addTicks(getTickHistory());
                List<TickEntry> tickStream = getStreamTicks();
                for (int i = 0; i < tickStream.size(); i++) {

                    chart.addTick(tickStream.get(i));

                    // Add Start spot
                    if (i == 3) {
                        chart.addStartSpot(tickStream.get(i - 1 ));

                        chart.addEntrySpot(tickStream.get(i));
                        chart.addBarrierLine(tickStream.get(i).getQuote());
                    } else if (i == 8) {
                        chart.addExitSpot(tickStream.get(i));
                        chart.removeAllBarrierLines();
                    }

                    if (i >= 3 && i <= 8) {
                        chart.addHighlightArea(
                                tickStream.get(i),
                                i % 2 == 0 ? Color.GREEN : Color.RED
                        );
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    public void addTick(int i) {
        TickEntry tick = new TickEntry(Long.valueOf(times[i]), Float.valueOf(prices[i]));
        this.chart.addTick(tick);
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

                for (int i = 0; i > times.length(); i++) {
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

    private List<TickEntry> getStreamTicks() {
        try {
            return this.getMockData("line-data-subscribe.json");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    String[] prices = new String[]{
            "112.354",
            "112.359",
            "112.358",
            "112.359",
            "112.359",
            "112.360",
            "112.364",
            "112.365",
            "112.365",
            "112.365",
            "112.365",
            "112.364",
            "112.364",
            "112.364",
            "112.363",
            "112.363",
            "112.362",
            "112.362",
            "112.363",
            "112.370",
            "112.370",
            "112.370",
            "112.374",
            "112.373",
            "112.373",
            "112.373",
            "112.372",
            "112.373",
            "112.373",
            "112.373",
            "112.371",
            "112.372",
            "112.372",
            "112.373",
            "112.373",
            "112.376",
            "112.376",
            "112.380",
            "112.377",
            "112.377",
            "112.376",
            "112.376",
            "112.376",
            "112.376",
            "112.379",
            "112.378",
            "112.378",
            "112.379",
            "112.379",
            "112.380"
    };
    String[] times = new String[] {
            "1507704654",
            "1507704655",
            "1507704661",
            "1507704662",
            "1507704675",
            "1507704678",
            "1507704679",
            "1507704680",
            "1507704682",
            "1507704686",
            "1507704689",
            "1507704692",
            "1507704696",
            "1507704700",
            "1507704701",
            "1507704710",
            "1507704711",
            "1507704724",
            "1507704727",
            "1507704730",
            "1507704731",
            "1507704733",
            "1507704741",
            "1507704742",
            "1507704744",
            "1507704745",
            "1507704746",
            "1507704748",
            "1507704749",
            "1507704750",
            "1507704752",
            "1507704753",
            "1507704754",
            "1507704760",
            "1507704761",
            "1507704764",
            "1507704766",
            "1507704767",
            "1507704768",
            "1507704770",
            "1507704771",
            "1507704774",
            "1507704776",
            "1507704781",
            "1507704782",
            "1507704786",
            "1507704791",
            "1507704794",
            "1507704798",
            "1507704801"
    };
}
