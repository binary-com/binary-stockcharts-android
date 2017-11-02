package com.binary.binarystockchartsandroid.notimportant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.binary.binarystockchartsandroid.BinaryCandleStickChartActivity;
import com.binary.binarystockchartsandroid.BinaryLineChartActivity;
import com.binary.binarystockchartsandroid.R;

import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        setTitle("MPAndroidChart Example");

        // initialize the utilities
//        Utils.init(this);

        ArrayList<ContentItem> objects = new ArrayList<ContentItem>();

        objects.add(new ContentItem("Line Chart", "A simple demonstration of the lineChart."));
        objects.add(new ContentItem("CandleStick Chart", "A simple demonstration of the candleStickChart."));

        MyAdapter adapter = new MyAdapter(this, objects);

        ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long arg3) {

        Intent i;

        switch (pos) {
            case 0:
                i = new Intent(this, BinaryLineChartActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, BinaryCandleStickChartActivity.class);
                startActivity(i);
                break;

        }

        overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
    }
}
