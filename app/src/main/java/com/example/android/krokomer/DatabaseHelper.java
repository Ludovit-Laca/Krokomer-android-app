package com.example.android.krokomer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "StepCounter7.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // vytvorí tabuľku steps

        String SQL_CREATE_TABLE_MESTO = "CREATE TABLE " + MyContrast.Steps.TABLE_NAME + "("
                + MyContrast.Steps.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MyContrast.Steps.COLUMN_DATUM + " TEXT, "
                + MyContrast.Steps.COLUMN_STEPS + " TEXT) ";
        db.execSQL(SQL_CREATE_TABLE_MESTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MyContrast.Steps.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // pridá do databazy
    public void addSteps(Steps s) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Steps.COLUMN_DATUM, s.getDatum());
        values.put(MyContrast.Steps.COLUMN_STEPS, s.getSteps());

        SQLiteDatabase db = getWritableDatabase();
        long newRowId = db.insert(
                MyContrast.Steps.TABLE_NAME,
                null,
                values);
        db.close();
    }

    // ziska tabuľku Steps pomocou dátumu
    public Steps getSteps(String date) {
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {MyContrast.Steps.COLUMN_ID, MyContrast.Steps.COLUMN_DATUM, MyContrast.Steps.COLUMN_STEPS};
        String selection = MyContrast.Steps.COLUMN_DATUM + "=? ";
        String[] selectionArgs = {"" + date};

        Cursor c = db.query(
                MyContrast.Steps.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (c.getCount() > 0 && c != null && c.moveToFirst()) {
            c.moveToFirst();

            Steps s = new Steps(
                    c.getInt(c.getColumnIndex(MyContrast.Steps.COLUMN_ID)),
                    date,
                    c.getString(c.getColumnIndex(MyContrast.Steps.COLUMN_STEPS)));
            c.close();
            db.close();
            return s;
        } else {
            return null;
        }
    }

    // updatne tabuľku Steps
    public void updateSteps(Steps s) {
        ContentValues values = new ContentValues();
        values.put(MyContrast.Steps.COLUMN_DATUM, s.getDatum());
        values.put(MyContrast.Steps.COLUMN_STEPS, s.getSteps());

        SQLiteDatabase db = getWritableDatabase();
        db.update(
                MyContrast.Steps.TABLE_NAME,
                values,
                MyContrast.Steps.COLUMN_DATUM + "= ?",
                new String[]{"" + s.getDatum()});
        db.close();
    }

    // zisti či existuje nejaký záznam v tabuľke so zadaným dátumom
    public boolean CheckIsDataAlreadyInDBorNot(String datum) {
        SQLiteDatabase sqldb = getWritableDatabase();
        String Query = "SELECT * FROM " + MyContrast.Steps.TABLE_NAME + " WHERE " + MyContrast.Steps.COLUMN_DATUM + " =  \"" + datum + "\";";
        Cursor cursor = sqldb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    // ziska všetko zo Steps
    public List<HashMap<String, String>> getSteps() {
        SQLiteDatabase db = getWritableDatabase();
        List<HashMap<String, String>> zoznam = new ArrayList<HashMap<String, String>>();
        Cursor c = db.rawQuery("select * from " + MyContrast.Steps.TABLE_NAME + " order by " + MyContrast.Steps.COLUMN_DATUM + " desc", null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("_id", c.getString(c.getColumnIndex(MyContrast.Steps.COLUMN_ID)));
                map.put("datum", c.getString(c.getColumnIndex(MyContrast.Steps.COLUMN_DATUM)));
                map.put("kroky", c.getString(c.getColumnIndex(MyContrast.Steps.COLUMN_STEPS)));
                zoznam.add(map);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return zoznam;
    }

    // vrati kurzor
    public Cursor VratKurzor() {
        SQLiteDatabase db = getWritableDatabase();
        String sstr = "select * from " + MyContrast.Steps.TABLE_NAME;
        Cursor c = db.rawQuery(sstr, null);
        c.moveToFirst();
        db.close();
        return c;
    }
}

