package com.unixonly.fooslive.fragment_utils;

import android.os.Bundle;
import android.support.annotation.Nullable;

public interface OnFragmentInteractionListener {
    /**
     * Used for communication between fragment and parent activity
     * @param action identifier for custom actions
     * @param args attached arguments to manipulate with
     */
    void onFragmentCallback(@FragmentCallback.Action int action, @Nullable Bundle args);
}
