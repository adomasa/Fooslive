package com.unixonly.fooslive.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.moandjiezana.toml.Toml;
import com.unixonly.fooslive.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Handle TOML configuration attributes from config file
 * Currently keeps memory during full application lifecycle
 */
public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static final int FATALITY_CONFIG_LOAD = 0;

    private static Toml sConfig;

    // Hide public parent constructor
    private ConfigManager() {}

    /**
     * Load data from configuration file
     * @param context activity context
     */
    public static void load(@NonNull Context context) {
        sConfig = new Toml();
        InputStream inputStream = context.getResources().openRawResource(R.raw.config);
        try {
            sConfig = sConfig.read(inputStream);
            inputStream.close();
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "Couldn't open configuration file. ");
            // Terminate application
            System.exit(FATALITY_CONFIG_LOAD);
        }
    }

    /**
     * Raises exception if configuration file doesn't have value for given key
     * @param key configuration attribute identifier
     */
    private static void isAvailable(String key) {
        if (!sConfig.contains(key)) {
            throw new Resources.NotFoundException("Configuration key not found " + key);
        }
    }

    /**
     * Retrieve parsed int attribute from config file
     * @param key attribute identifier
     * @return parsed attribute int value
     */
    public static int getInt(@NonNull String key) {
        isAvailable(key);
        long longValue = sConfig.getLong(key);
        return (int)longValue;
    }

    /**
     * Retrieve list attribute from config file
     * @param key attribute identifier
     * @return not generic list object
     */
    public static List getList(@NonNull String key) {
        isAvailable(key);
        return sConfig.getList(key);
    }

    /**
     * Retrieve double attribute from config file
     * @param key attribute identifier
     * @return attribute double value
     */
    public static double getDouble(String key) {
        isAvailable(key);
        return sConfig.getLong(key);
    }
}
