package com.binary.binarystockchart.data;

import android.graphics.drawable.Drawable;

import com.binary.binarystockchart.utils.ChartUtils;
import com.github.mikephil.charting.data.CandleEntry;

/**
 * Created by morteza on 10/25/2017.
 */

public class BinaryCandleEntry {
    private Long epoch;
    private Float high;
    private Float low;
    private Float open;
    private Float close;

    public BinaryCandleEntry(Long epoch, float high, float low, float open, float close) {
        this.setEpoch(epoch);
        this.setHigh(high);
        this.setLow(low);
        this.setOpen(open);
        this.setClose(close);
    }

    public CandleEntry getCandleEntry(Long epochReference, Integer granularity) {
        return new CandleEntry(
                ChartUtils.convertEpochToChartX(this.epoch, epochReference, granularity),
                this.high, this.low, this.open, this.close);
    }

    public CandleEntry getCandleEntry(Long epochReference) {
        return this.getCandleEntry(epochReference, 60);
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public Float getHigh() {
        return high;
    }

    public void setHigh(Float high) {
        this.high = high;
    }

    public Float getLow() {
        return low;
    }

    public void setLow(Float low) {
        this.low = low;
    }

    public Float getOpen() {
        return open;
    }

    public void setOpen(Float open) {
        this.open = open;
    }

    public Float getClose() {
        return close;
    }

    public void setClose(Float close) {
        this.close = close;
    }
}
