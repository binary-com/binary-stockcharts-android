package com.binary.binarystockchart.components;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.binary.binarystockchart.R;
import com.binary.binarystockchart.charts.BinaryCandleStickChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by morteza on 11/6/2017.
 */

public class CandleMarkerView extends MarkerView {

    private TextView tvContent;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CandleMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(getFormattedContext(ce));
        } else {

            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    private Spanned getFormattedContext(CandleEntry e) {
        String text;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getContext().getString(R.string.candle_marker_view_date_format));
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        date.setTime((((BinaryCandleStickChart) this.getChartView()).getEpochReference()
                + Float.valueOf(e.getX()).longValue()) * 1000);
        text = String.format(getContext().getString(R.string.candle_marker_view_text),
                dateFormat.format(date),
                String.valueOf(e.getHigh()),
                String.valueOf(e.getOpen()),
                String.valueOf(e.getLow()),
                String.valueOf(e.getClose())
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() * 2);
    }
}
