package com.example.android.krokomer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends CursorAdapter {

    public CustomCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from
                (context).inflate(R.layout.prehlad_list_layout, // ako naplnat
                parent, // čo naplnať
                false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvDatum = (TextView) view.findViewById(R.id.datum1);
        TextView tvSteps = (TextView) view.findViewById(R.id.steps1);

        String datum = cursor.getString(cursor.getColumnIndex(MyContrast.Steps.COLUMN_DATUM));
        int steps = (int) Float.parseFloat(cursor.getString(cursor.getColumnIndex(MyContrast.Steps.COLUMN_STEPS)));

        // ak bude goal splnený.. tak bude farba zelená inak bude červená.
        if (steps < 6000) {
            tvSteps.setTextColor(Color.RED);  // farba textu
        } else {
            tvSteps.setBackgroundColor(Color.GREEN);
        }
        tvDatum.setText(datum);
        tvSteps.setText("" + steps);
    }
}
