package com.binary.binarystockchartsandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.binary.api.ApiWrapper;
import com.binary.api.models.requests.TickHistoryRequest;
import com.binary.api.models.responses.TickHistoryResponse;
import com.binary.api.models.responses.TickResponse;
import com.binary.binarystockchart.Indecators.SimpleMovingAverageIndicator;
import com.binary.binarystockchart.charts.BinaryLineChart;
import com.binary.binarystockchart.data.TickEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class LineCombineChartActivity extends AppCompatActivity {

    BinaryLineChart chart;
    ApiWrapper api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_combine_chart);

        this.chart = (BinaryLineChart) findViewById(R.id.binaryRealTimeCombineLineChart);
        this.chart.setAutoScaleMinMaxEnabled(true);

        this.api = ApiWrapper.build("10", "en", "wss://frontend.binaryws.com/websockets/v3");

        TickHistoryRequest tickHistory = new TickHistoryRequest("R_50", "latest");
        tickHistory.setSubscribe(1);
        tickHistory.setCount(500);
        this.chart.addIndicator(new SimpleMovingAverageIndicator("SMA"));

        api.sendRequest(tickHistory)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if( response instanceof TickHistoryResponse) {
                                this.chart.addTicks(
                                        this.convertHistoryResToTickList((TickHistoryResponse)response)
                                );

                            } else if (response instanceof TickResponse) {
                                TickResponse tick = (TickResponse) response;
                                this.chart.addTick(new TickEntry(
                                        Long.valueOf(tick.getTick().getEpoch()),
                                        Float.valueOf(tick.getTick().getQuote())
                                ));
                            }
                        }
                );
    }

    private List<TickEntry> convertHistoryResToTickList(TickHistoryResponse response) {
        if(response.getError() != null) {
            return null;
        }
        List<Integer> times = response.getHistory().getTimes();
        List<BigDecimal> prices = response.getHistory().getPrices();

        List<TickEntry> entries = new ArrayList<>();

        for (int i = 0; i < times.size(); i++) {
            TickEntry entry = new TickEntry(
                    times.get(i).longValue(),
                    prices.get(i).floatValue()
            );

            entries.add(entry);
        }

        return entries;
    }

    @Override
    public void onDestroy() {
        this.api.closeConnection();
        super.onDestroy();
    }
}
