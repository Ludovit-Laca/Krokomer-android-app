package com.example.android.krokomer;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class PrehladActivity extends AppCompatActivity {

    CustomCursorAdapter myAdapter;
    DatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prehlad);
        // odstranim shadow pod toolbarom a setnem orientaciu
        getSupportActionBar().setElevation(0);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // ziskanie DBHelpera
        mDatabaseHelper = new DatabaseHelper(getApplicationContext());
        PripojAdapter();
    }

    // priradí ListView vlastny adapter
    private void PripojAdapter() {
        Cursor myCursor = mDatabaseHelper.VratKurzor();
        myAdapter = new CustomCursorAdapter(this, myCursor, 0);
        ListView lvItems = (ListView) findViewById(R.id.listviewPrehlad);
        lvItems.setAdapter(myAdapter);
    }

    // presmeruje do MainActivity
    public void otvorDen(View view) {
        startActivity(new Intent(PrehladActivity.this, MainActivity.class));
        finish();
    }

    // pridá menu do aktivity
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
}
