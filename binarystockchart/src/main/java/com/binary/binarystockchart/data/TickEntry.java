package com.binary.binarystockchart.data;

import com.binary.binarystockchart.interfaces.data.IEntry;

/**
 * Created by morteza on 10/10/2017.
 */

public class TickEntry implements IEntry {

    private Long epoch;
    private Float quote;

    public TickEntry(Long epoch, Float quote) {
        this.setEpoch(epoch);
        this.setQuote(quote);
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    public Float getQuote() {
        return quote;
    }

    public void setQuote(Float quote) {
        this.quote = quote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TickEntry)) return false;

        TickEntry tick = (TickEntry) o;

        if (getEpoch() != null ? !getEpoch().equals(tick.getEpoch()) : tick.getEpoch() != null)
            return false;
        return getQuote() != null ? getQuote().equals(tick.getQuote()) : tick.getQuote() == null;

    }

    @Override
    public int hashCode() {
        int result = getEpoch() != null ? getEpoch().hashCode() : 0;
        result = 31 * result + (getQuote() != null ? getQuote().hashCode() : 0);
        return result;
    }
}
