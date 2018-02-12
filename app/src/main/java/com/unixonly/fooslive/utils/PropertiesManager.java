package com.unixonly.fooslive.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.unixonly.fooslive.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Handles properties from config file
 * Currently keeps memory during full application lifecycle
 */
public class PropertiesManager {
    private static final String TAG = "PropertiesManager";
    private static Properties sProperties;

    /**
     * Load data from configuration file
     * @param context activity context
     */
    public static void load(Context context) {
        sProperties = new Properties();
        InputStream inputStream = context.getResources().openRawResource(R.raw.config);
        try {
            sProperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            // Set null to force nullptr exception on call if failed to read cfg file
            sProperties = null;
            Log.e(TAG, "Couldn't open configuration file. " + e.getMessage());
        }
    }


    /**
     * Retrieves raw value from config file
     * @param key property identifier
     * @return Config attribute value or null if value not found
     */
    public static String getString(String key) {
        String property = sProperties.getProperty(key);

        if (property == null)
            throw new Resources.NotFoundException("Property not found in configuration file. Key: "
                    + key);

        return property;
    }

    /**
     * Retrieves parsed int property from config file
     * @param key property identifier
     * @return Parsed int property
     */
    public static int getInt(String key)
    {
        String property = getString(key);
        return Integer.valueOf(property);
    }

    /**
     * Retrieves parsed float property from config file
     * @param key property identifier
     * @return Parsed float property
     */
    public static float getFloat(String key)
    {
        String property = getString(key);
        return Float.valueOf(property);
    }

}
