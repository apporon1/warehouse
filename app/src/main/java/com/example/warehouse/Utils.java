package com.example.warehouse;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private static final String PREFS_NAME = "WarehousePrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_THEME = "theme";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NOTIFICATIONS = "notifications";
    public static final String EXTRA_ENTITY_ID = "entity_id";

    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_EMPLOYEE = "employee";
    public static final String TYPE_SUPPLIER = "supplier";
    public static final String TYPE_CARRIER = "carrier";
    public static final String TYPE_WAREHOUSE = "warehouse";
    public static final String TYPE_DOCUMENT = "document";

    private final Context context;
    private final SharedPreferences prefs;

    public Utils(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean register(String email, String password) {
        if (prefs.contains(email)) {
            return false;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(email, password);
        editor.apply();
        return true;
    }

    public boolean login(String email, String password) {
        String storedPassword = prefs.getString(email, null);
        if (storedPassword != null && storedPassword.equals(password)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_EMAIL, email);
            editor.apply();
            return true;
        }
        return false;
    }

    public boolean isLoggedIn() {
        return prefs.contains(KEY_EMAIL);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_EMAIL);
        editor.apply();
    }

    public String getTheme() {
        return prefs.getString(KEY_THEME, "Системная");
    }

    public void setTheme(String theme) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_THEME, theme);
        editor.apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "Русский");
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_NOTIFICATIONS, enabled);
        editor.apply();
    }
}