package com.example.alec.MaintenanceKeeper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    private Spinner fontSizeSpinner;
    private Spinner fontColorSpinner;
    private SharedPreferences.Editor editor;
    private ArrayAdapter<CharSequence> fontSizeAdapter;
    private ArrayAdapter<CharSequence> fontColorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar();

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fontSizeSpinner = (Spinner) findViewById(R.id.spFontSize);
        fontColorSpinner = (Spinner) findViewById(R.id.spFontColor);

        fontSizeAdapter = ArrayAdapter.createFromResource(this, R.array.fontSizeArray, R.layout.support_simple_spinner_dropdown_item);
        fontSizeSpinner.setAdapter(fontSizeAdapter);

        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                editor.putString("fontSize", adapterView.getItemAtPosition(i).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(SettingsActivity.this, "Please select a font size.", Toast.LENGTH_SHORT).show();
            }
        });

        fontColorAdapter = ArrayAdapter.createFromResource(this, R.array.fontColorArray, R.layout.support_simple_spinner_dropdown_item);
        fontColorSpinner.setAdapter(fontColorAdapter);
        fontColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                editor.putString("fontColor", adapterView.getItemAtPosition(i).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(SettingsActivity.this, "Please select a font color.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        fontSizeSpinner.setSelection(fontSizeAdapter.getPosition(sharedPreferences.getString("fontSize", "Medium")));
        fontColorSpinner.setSelection(fontColorAdapter.getPosition(sharedPreferences.getString("fontColor", "Black")));
    }

    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }
    }
}
