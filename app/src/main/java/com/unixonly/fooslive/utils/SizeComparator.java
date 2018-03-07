package com.unixonly.fooslive.utils;

import android.util.Size;

import java.util.Comparator;

//TODO: remove or make use of redundant class
public class SizeComparator implements Comparator<Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
        return Long.signum( (long)lhs.getWidth() * lhs.getHeight() -
                (long)rhs.getWidth() * rhs.getHeight() );
    }
}
