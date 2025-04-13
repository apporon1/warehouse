package com.example.warehouse;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner spinnerTheme;
    private Spinner spinnerLanguage;
    private Switch switchNotifications;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        spinnerTheme = findViewById(R.id.spinner_theme);
        spinnerLanguage = findViewById(R.id.spinner_language);
        switchNotifications = findViewById(R.id.switch_notifications);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Настройки");

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Светлая", "Темная"});
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Русский", "English"});
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        btnSave.setOnClickListener(v -> {
            String theme = spinnerTheme.getSelectedItem().toString();
            String language = spinnerLanguage.getSelectedItem().toString();
            boolean notifications = switchNotifications.isChecked();

            // Здесь можно сохранить настройки, например, в SharedPreferences
            Toast.makeText(this, "Настройки сохранены: " + theme + ", " + language + ", Уведомления: " + notifications, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}