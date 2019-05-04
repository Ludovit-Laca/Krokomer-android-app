package com.example.android.krokomer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private static int krokyMain = 0;
    private static TextView tv_steps;
    private static TextView den;
    private static TextView kcalTv;

    private BarChart barChart;
    private DatabaseHelper mDatabaseHelper;
    private SimpleDateFormat df;
    private SimpleDateFormat df2;
    private static String aktualnyDatum;
    private static String aktualnyDatum2;
    private static Date c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // odstranim shadow pod toolbarom a setnem orientaciu
        getSupportActionBar().setElevation(0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nastavViews();

        // ziskanie DBHelpera
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());

        // nastavi workera
        PeriodicWorkRequest uploadWorkRequest = new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueue(uploadWorkRequest);

        nastavGraf();
    }

    // nastavim Views, datum ...
    private void nastavViews() {
        tv_steps = (TextView) findViewById(R.id.textview1);
        den = (TextView) findViewById(R.id.time1);
        kcalTv = (TextView) findViewById(R.id.kCalTv);
        df = new SimpleDateFormat("EEE, d. MMM");
        df2 = new SimpleDateFormat("dd-MMM-yyyy");
        c = Calendar.getInstance().getTime();
        aktualnyDatum = df.format(c);
        aktualnyDatum2 = df2.format(c);
    }

    // nastavi graf pomocou MPandroidChart
    private void nastavGraf() {
        barChart = (BarChart) findViewById(R.id.bargraph);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> theDates = new ArrayList<>();

        // ziskame zaznamy z tabuľky Steps a naplnime graf
        List<HashMap<String, String>> zoznam = mDatabaseHelper.getSteps();
        HashMap<String, String> mapa = new HashMap<>();
        for (int i = 0; i < zoznam.size(); i++) {
            if (i == 3) break;
            mapa = zoznam.get(i);
            theDates.add(mapa.get("datum"));
            barEntries.add(new BarEntry(Float.parseFloat(mapa.get("kroky")), i));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Steps");
        int redColorValue = Color.parseColor("#ff1744");
        barDataSet.setColor(redColorValue);
        BarData theData = new BarData(theDates, barDataSet);

        barChart.setData(theData);
        barChart.getLegend().setEnabled(false);
        barChart.setDescription(null);
    }

    // metoda na nastavenie textu v maine
    protected static void setKroky(float ikroky) {
        krokyMain = (int) ikroky;
        tv_steps.setText("" + krokyMain);
        den.setText(aktualnyDatum);
        vypocitajKcal(krokyMain);
    }

    // metoda na vypocitanie Kcal
    private static void vypocitajKcal(int ikrokyMain) {
        float kCal = ikrokyMain / 20;
        kcalTv.setText("" + kCal + " kcal !");
    }

    // otvori aktivitu PrehladActivity
    public void otvorPrehlad(View view) {
        startActivity(new Intent(MainActivity.this, PrehladActivity.class));
        finish();
    }

    // prida menu do aktivity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // čo sa má vykonať po kliknutí na položku
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Toast.makeText(getBaseContext(), "Made by Ľudovít Laca", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ziska kroky z databazy a ukaze na obbazovke
    @Override
    protected void onResume() {
        super.onResume();
        Steps ss = mDatabaseHelper.getSteps(aktualnyDatum2);
        if (ss != null) {
            setKroky(Float.parseFloat(ss.getSteps()));
        } else {
            setKroky(UploadWorker.counter);
        }

    }


}
