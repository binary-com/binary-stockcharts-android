package com.binary.binarystockchart.interfaces.charts;

import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;

/**
 * Created by morteza on 2/3/2018.
 */

public interface IBinaryChart <T extends BarLineScatterCandleBubbleData> {

    void addStartSpot(Long epoch);
    void addEntrySpot(Long epoch);
    void addExitSpot(Long epoch);
    void addHighlightArea(Long epoch, int areaColor);
}
