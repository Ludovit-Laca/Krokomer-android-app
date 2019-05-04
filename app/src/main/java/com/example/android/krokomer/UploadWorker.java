package com.example.android.krokomer;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.MODE_PRIVATE;

public class UploadWorker extends Worker implements SensorEventListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private Sensor stepDetectorSensor;

    private DatabaseHelper mDatabaseHelper;

    private Date c;
    private SimpleDateFormat df;
    private String aktualnyDatum;
    protected static int counter;

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        df = new SimpleDateFormat("dd-MMM-yyyy"); // format použivany v databaze

        // ziskam default senzory
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // zaregistrujem listeners
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        loadData();
    }

    @NonNull
    @Override
    public Result doWork() {
        // ziskame aktualny datum
        c = Calendar.getInstance().getTime();
        aktualnyDatum = df.format(c);
        Steps s;
        // ak v databaze sa už nachadza zaznam s aktualnym datum
        if (mDatabaseHelper.CheckIsDataAlreadyInDBorNot(aktualnyDatum)) {
            // aktualizujeme zaznam v databaze
            s = new Steps(1, aktualnyDatum, String.valueOf(counter));
            mDatabaseHelper.updateSteps(s);
        } else {
            // inak pridame novy zaznam do databazy
            // resetneme counter a ulozime hodnotu do SharedPrefs
            counter = 0;
            saveData();
            s = new Steps(1, aktualnyDatum, String.valueOf(counter));
            mDatabaseHelper.addSteps(s);
        }
        return Result.success();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // nacitam hodnotu zo SharedPrefs
        loadData();
        // pripocitam pohyb
        counter++;
        // savnem hodnotu do SharedPrefs
        saveData();
        MainActivity.setKroky(counter);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // metoda na ukladanie pomocou SharedPrefs
    public void saveData() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(TEXT, counter);
        editor.apply();
    }

    // metoda na načitanie pomocou SharedPrefs
    public void loadData() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        counter = sharedPreferences.getInt(TEXT, 0);
    }

}
