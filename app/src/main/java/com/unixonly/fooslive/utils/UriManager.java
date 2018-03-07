package com.unixonly.fooslive.utils;

import android.content.Context;
import android.net.Uri;

public class UriManager {
    private Context mContext;

    public UriManager(Context context) {
        mContext = context;
    }

    /**
     * Generate URI based on the name
     * @param name file name without extension from raw folder
     * @return raw resource URI
     */
    public Uri uriFromRaw(String name) {
        return new Uri.Builder().scheme("android.resource")
                .authority(mContext.getPackageName())
                .path("/raw/" + name)
                .build();
    }
}
