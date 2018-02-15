package com.unixonly.fooslive.Util;

import android.util.Size;

import java.util.Comparator;

/**
 * Created by paulius on 1/24/18.
 */

public class SizeComparator implements Comparator<Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
        return Long.signum( (long)lhs.getWidth() * lhs.getHeight() -
                (long)rhs.getWidth() * rhs.getHeight() );
    }
}
