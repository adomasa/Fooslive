package com.unixonly.fooslive.utils;

import android.content.Context;
import android.net.Uri;

public class UriManager {

    private UriManager() {}

    /**
     * Generate URI based on the name
     * @param context app context for package name extraction
     * @param name file name without extension from raw folder
     * @return raw resource URI
     */
    public static Uri uriFromRaw(Context context, String name) {
        return new Uri.Builder().scheme("android.resource")
                .authority(context.getPackageName())
                .path("/raw/" + name)
                .build();
    }
}
