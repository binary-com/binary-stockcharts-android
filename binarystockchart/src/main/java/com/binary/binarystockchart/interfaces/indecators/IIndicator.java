package com.binary.binarystockchart.interfaces.indecators;

import com.binary.binarystockchart.data.BinaryCandleEntry;
import com.binary.binarystockchart.data.TickEntry;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

/**
 * Created by morteza on 1/7/2018.
 */

public interface IIndicator {

    /**
     * Remove all chartData from chart
     *
     */
    void destroy();

    /**
     * Call this method to let the Indicator know that the underlying data has
     * changed. Calling this performs all necessary recalculations needed when
     * the contained data has changed.
     */
    void notifyDataChanged();

    /**
     * Sets ChartData
     */
    void setChartData (CombinedData chartData);

    /**
     * Returns ChartData
     */
    CombinedData getChartData ();
}

