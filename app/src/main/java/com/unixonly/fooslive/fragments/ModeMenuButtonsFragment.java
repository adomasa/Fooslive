package com.unixonly.fooslive.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unixonly.fooslive.GameActivity;
import com.unixonly.fooslive.R;
import com.unixonly.fooslive.databinding.FragmentItemsModeMenuBinding;
import com.unixonly.fooslive.fragment_interaction.FragmentCallback;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ModeMenuButtonsFragment extends Fragment {
    public static final String TAG = "ModeMenuButtonsFragment";

    private static final int PICK_VIDEO_REQUEST = 0;
    private static final int OPEN_CAMERA_REQUEST = 1;

    FragmentItemsModeMenuBinding mBinding;
    AlertDialog mRequestDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Update top bar title
        Bundle args = new Bundle();
        args.putInt(getString(R.string.argument_title), R.string.title_menu_modes);
        ((MainMenuFragment)getParentFragment())
                .mListener.onFragmentCallback(FragmentCallback.ACTION_SET_TITLE, args);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_items_mode_menu, container,
                false);

        mBinding.buttonLiveGame.setOnClickListener((View v) -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) startGameActivity(null);
            // API 23+ demands runtime requests
            else getCameraPermission();
        });

        mBinding.buttonRecordedGame.setOnClickListener((View v) -> {
            Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_PICK);
            videoIntent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(videoIntent, PICK_VIDEO_REQUEST);
        });

        return mBinding.getRoot();
    }

    /**
     * Handle camera permission request if needed
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getCameraPermission() {
        if (getContext().checkSelfPermission(Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // already has permission
            startGameActivity(null);
            return;
        }
        // need to request permission
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // explain to the user why we need to use the camera
            getRequestDialog().show();
            return;
        }

        //Finally request permissions with the list of permissions and Id
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{ Manifest.permission.READ_CONTACTS },
                OPEN_CAMERA_REQUEST);
    }


    /**
     * Build custom alert dialog for camera permission request explanation
     * @return AlertDialog instance
     */
    private AlertDialog getRequestDialog() {
        if (mRequestDialog != null) return mRequestDialog;

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.title_camera_request_explanation))
                .setMessage(getString(R.string.msg_camera_request_explanation))
                .setNeutralButton(getString(R.string.action_dismiss), (dialog1, which) -> {
                    dialog1.dismiss();
                    // Request permission after dismissed dialog
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            OPEN_CAMERA_REQUEST);
                    });

        return dialog.create();
    }

    /**
     * Start GameActivity
     * @param data contains video uri for GameActivity
     */
    private void startGameActivity(@Nullable Uri data) {

        Intent intent = new Intent(getActivity(), GameActivity.class);
        // Set video uri as game activity intent data
        if (data != null) intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PICK_VIDEO_REQUEST) {
            Log.e(TAG, "Unknown activity request result: " + requestCode );
            return;
        }

        if (resultCode == RESULT_OK) {
            startGameActivity(data.getData());
            return;
        }

        if (resultCode == RESULT_CANCELED) return;

        Snackbar.make(mBinding.getRoot(), getString(R.string.error_unknown), Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != OPEN_CAMERA_REQUEST) return;

        if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) startGameActivity(null);
        else {
            Snackbar.make(mBinding.getRoot(),
                    getString(R.string.error_camera_access_missing),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

    }
}
