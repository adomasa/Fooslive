package com.unixonly.fooslive.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.unixonly.fooslive.R;
import com.unixonly.fooslive.utils.UriUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @source https://stackoverflow.com/questions/25046662
 * /how-can-i-add-my-apps-custom-ringtones-in-res-raw-folder-to-a-ringtonepreferenc
 */
public class ExtraRingtonePreference extends DialogPreference {

    private Context mContext;
    private String mValue;
    private Ringtone mRingtone;
    private int mRingtoneType;
    private boolean mShowSilent;
    private boolean mShowDefault;
    private CharSequence[] mExtraRingtones;
    private CharSequence[] mExtraRingtonesTitles;

    public ExtraRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        final TypedArray attributes = context.obtainStyledAttributes(attrs,
                R.styleable.ExtraRingtonePreference, 0, 0);

        mRingtoneType = attributes.getInt(R.styleable.ExtraRingtonePreference_ringtoneType,
                RingtoneManager.TYPE_RINGTONE);
        mShowDefault = attributes.getBoolean(R.styleable.ExtraRingtonePreference_showDefault,
                true);
        mShowSilent = attributes.getBoolean(R.styleable.ExtraRingtonePreference_showSilent,
                true);
        mExtraRingtones = attributes.getTextArray(
                R.styleable.ExtraRingtonePreference_extraRingtones);
        mExtraRingtonesTitles = attributes.getTextArray(
                R.styleable.ExtraRingtonePreference_extraRingtoneTitles);
        attributes.recycle();
    }

    public ExtraRingtonePreference(Context context) {
        this(context, null);
    }

    public String getValue() {
        return mValue;
    }

    /**
     * Retrieve system-wide sounds based on their type
     * @param type sound type e.g. alerts, notifications, etc.
     * @return Map collection of ringtones titles and uris
     */
    private Map<String, Uri> getSounds(int type) {

        RingtoneManager ringtoneManager = new RingtoneManager(mContext);
        ringtoneManager.setType(type);
        Cursor cursor = ringtoneManager.getCursor();

        Map<String, Uri> list = new TreeMap<>();
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri notificationUri = ringtoneManager.getRingtoneUri(cursor.getPosition());

            list.put(notificationTitle, notificationUri);
        }

        return list;
    }

    /**
     * Retrieve custom ringtone title
     * @param name custom ringtone name
     * @return title on success, null on failed search
     */
    public String getExtraRingtoneTitle(CharSequence name) {
        if (mExtraRingtones == null || mExtraRingtonesTitles == null) return null;

        int index = Arrays.asList(mExtraRingtones).indexOf(name);
        return mExtraRingtonesTitles[index].toString();

    }

    @Override
    public CharSequence getSummary() {
        if (mValue == null) return super.getSummary();
        // Check whether value has silent mode indication(empty string)
        if (mValue.isEmpty()) return mContext.getString(R.string.sound_silent_title);

        Uri valueUri = Uri.parse(mValue);

        // Look through custom ringtones from raw folder
        if (mExtraRingtones != null && mExtraRingtonesTitles != null) {
            for (int i = 0; i < mExtraRingtones.length; i++) {
                Uri uriExtra = UriUtils.uriFromRaw(mContext, mExtraRingtones[i].toString());
                if (uriExtra.equals(valueUri)) return mExtraRingtonesTitles[i].toString();
            }
        }

        // Look through system-wide ringtones
        Ringtone ringtone = RingtoneManager.getRingtone(mContext, valueUri);
        if (ringtone != null) {
            String title = ringtone.getTitle(mContext);
            if (!TextUtils.isEmpty(title)) return title;
        }

        return super.getSummary();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        final Map<String, Uri> sounds = new LinkedHashMap<>();

        // Collect custom ringtones
        if (mExtraRingtones != null) {
            for (CharSequence extraRingtone : mExtraRingtones) {
                Uri uri = UriUtils.uriFromRaw(mContext, extraRingtone.toString());
                String title = getExtraRingtoneTitle(extraRingtone);

                sounds.put(title, uri);
            }
        }

        // Add silent option
        if (mShowSilent) sounds.put(mContext.getString(R.string.sound_silent_title), Uri.parse(""));

        // Get default ringtone
        if (mShowDefault) {
            Uri uriDefault = RingtoneManager.getDefaultUri(mRingtoneType);
            Ringtone ringtoneDefault = RingtoneManager.getRingtone(mContext, uriDefault);
            if (ringtoneDefault != null) sounds.put(ringtoneDefault.getTitle(mContext), uriDefault);
        }

        // Collect system-wide ringtones
        sounds.putAll(getSounds(mRingtoneType));

        final String[] titleArray = sounds.keySet().toArray(new String[0]);
        final Uri[] uriArray = sounds.values().toArray(new Uri[0]);
        // Determine current ringtone position in dialog list
        int index = mValue != null ? Arrays.asList(uriArray).indexOf(Uri.parse(mValue)) : -1;

        builder.setSingleChoiceItems(titleArray, index, (DialogInterface dialog, int which) -> {
            // On dialog item click

            if (mRingtone != null) mRingtone.stop();

            Uri uriNewRingtone = uriArray[which];

            if (uriNewRingtone == null) {
                mValue = null;
                return;
            }

            // If user chose silent mode, halt
            if (uriNewRingtone.toString().isEmpty()) {
                mValue = uriNewRingtone.toString();
                return;
            }

            mRingtone = RingtoneManager.getRingtone(mContext, uriNewRingtone);
            if (mRingtone != null) mRingtone.play();
        })
                .setPositiveButton(R.string.action_save, this)
                .setNegativeButton(R.string.action_cancel, this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (mRingtone != null) mRingtone.stop();

        if (positiveResult && callChangeListener(mValue)) {
            persistString(mValue);
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            mValue = getPersistedString("");
            return;
        }

        if (mExtraRingtones != null && defaultValue != null && !defaultValue.toString().isEmpty()) {
            int index = Arrays.asList(mExtraRingtones).indexOf((CharSequence) defaultValue);

            mValue = (index >= 0) ? UriUtils.uriFromRaw(mContext, defaultValue.toString()).toString() :
                    (String)defaultValue;
        } else
            mValue = (String)defaultValue;

        persistString(mValue);
    }
}