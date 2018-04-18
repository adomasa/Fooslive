package com.unixonly.fooslive.config;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.moandjiezana.toml.Toml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Handles attributes from TOML config file
 * Currently keeps memory during full application lifecycle
 */
public class ConfigManager {
    private static final String TAG = "ConfigManager";

    private static Toml sConfig;

    // Hide public parent constructor
    private ConfigManager() {}

    /**
     * Load data from configuration file
     * @param context activity context
     * @throws IOException on fail to process configuration file
     */
    public static void load(@NonNull Context context, int configResId) throws IOException {
        sConfig = new Toml();
        InputStream inputStream = context.getResources().openRawResource(configResId);
        try {
            sConfig = sConfig.read(inputStream);
            inputStream.close();
        } catch (IOException | IllegalStateException e) {
            Log.e(TAG, "Couldn't open configuration file. ");
            throw e;
        }
    }

    /**
     * Raises exception if configuration file doesn't have value for given key
     * @param key configuration attribute identifier
     */
    private static void isAvailable(@NonNull String key) {
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
    @SuppressWarnings("unchecked")
    public static List getList(@NonNull String key) {
        isAvailable(key);
        return sConfig.getList(key);
    }

    /**
     * Retrieve generified float list using unchecked cast
     * @param key attribute identifier
     * @return generified float list
     */
    @SuppressWarnings("unchecked")
    public static List<Float> getFloatList(@NonNull String key) {
        return (List<Float>)getList(key);
    }

    /**
     * Retrieve generified integer list using unchecked cast
     * @param key attribute identifier
     * @return generified integer list
     */
    @SuppressWarnings("unchecked")
    public static List<Integer> getIntList(@NonNull String key) {
        return (List<Integer>)getList(key);
    }
    /**
     * Retrieve double attribute from config file
     * @param key attribute identifier
     * @return attribute double value
     */
    public static double getDouble(@NonNull String key) {
        isAvailable(key);
        return sConfig.getLong(key);
    }

    /**
     * Retrieve double attribute from config file
     * @param key attribute identifier
     * @return attribute double value
     */
    public static float getFloat(@NonNull String key) {
        isAvailable(key);
        return (float)sConfig.getLong(key);
    }

    /**
     * Retrieve generified long list using unchecked cast
     * @param key attribute identifier
     * @return generified long list
     */
    @SuppressWarnings("unchecked")
    public static List<Long> getLongList(@NonNull String key) {
        return (List<Long>)getList(key);
    }
}
