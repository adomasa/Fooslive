package com.unixonly.fooslive.utils;

import android.content.Context;
import android.net.Uri;

/**
 * Created by ramu on 11/02/2018.
 */

public class UriManager {
    private Context mContext;

    public UriManager(Context context) {
        this.mContext = context;
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
