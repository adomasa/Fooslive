package com.unixonly.fooslive;


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


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @source https://stackoverflow.com/questions/25046662/how-can-i-add-my-apps-custom-ringtones-in-res-raw-folder-to-a-ringtonepreferenc
 * @author https://stackoverflow.com/users/661589/gavriel
 * @status Not completely adapted
 */
public class ExtraRingtonePreference extends DialogPreference {

    private Context mContext;
    private String mValue;
    private Ringtone mRingtone;
    private int mRingtoneType;
    private boolean mShowSilent;
    private boolean mShowDefault;
    private CharSequence[] mExtraRingtones;
    private CharSequence[] mExtraRingtoneTitles;

    public ExtraRingtonePreference(Context context, AttributeSet attrs) {

        super(context, attrs);

        mContext = context;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtraRingtonePreference, 0, 0);

        mRingtoneType = a.getInt(R.styleable.ExtraRingtonePreference_ringtoneType, RingtoneManager.TYPE_RINGTONE);
        mShowDefault = a.getBoolean(R.styleable.ExtraRingtonePreference_showDefault, true);
        mShowSilent = a.getBoolean(R.styleable.ExtraRingtonePreference_showSilent, true);
        mExtraRingtones = a.getTextArray(R.styleable.ExtraRingtonePreference_extraRingtones);
        mExtraRingtoneTitles = a.getTextArray(R.styleable.ExtraRingtonePreference_extraRingtoneTitles);

        a.recycle();
    }

    public ExtraRingtonePreference(Context context) {
        this(context, null);
    }

    public String getValue() {
        return mValue;
    }

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

    private Uri uriFromRaw(String name) {
        return Uri.parse("android.resource://" + mContext.getPackageName() + "/raw/" + name);
    }

    public String getExtraRingtoneTitle(CharSequence name) {
        if (mExtraRingtones != null && mExtraRingtoneTitles != null) {
            int index = Arrays.asList(mExtraRingtones).indexOf(name);
            return mExtraRingtoneTitles[index].toString();
        }

        return null;
    }

    @Override
    public CharSequence getSummary() {

        String ringtoneTitle = null;

        if (mValue == null) return super.getSummary();

        if (mValue.isEmpty()) ringtoneTitle = mContext.getString(R.string.sound_silent_title);

        Uri valueUri = Uri.parse(mValue);

        // titleFromExtraRingtones
        if (ringtoneTitle == null && mExtraRingtones != null && mExtraRingtoneTitles != null) {

            for (int i = 0; i < mExtraRingtones.length; i++) {
                Uri uriExtra = uriFromRaw(mExtraRingtones[i].toString());
                if (uriExtra.equals(valueUri)) {
                    ringtoneTitle = mExtraRingtoneTitles[i].toString();
                    break;
                }
            }
        }


        // title from system-wide ringtones
        if (ringtoneTitle == null) {
            Ringtone ringtone = RingtoneManager.getRingtone(mContext, valueUri);
            if (ringtone != null) {
                String title = ringtone.getTitle(mContext);
                if (!TextUtils.isEmpty(title)) {
                    ringtoneTitle = title;
                }
            }
        }

        return (ringtoneTitle != null) ? ringtoneTitle : super.getSummary();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        final Map<String, Uri> sounds = new LinkedHashMap<>();

        if (mExtraRingtones != null) {
            for (CharSequence extraRingtone : mExtraRingtones) {
                Uri uri = uriFromRaw(extraRingtone.toString());
                String title = getExtraRingtoneTitle(extraRingtone);

                sounds.put(title, uri);
            }
        }

        if (mShowSilent)
            sounds.put(mContext.getString(R.string.sound_silent_title), Uri.parse(""));

        if (mShowDefault) {
            Uri uriDefault = RingtoneManager.getDefaultUri(mRingtoneType);
            if (uriDefault != null) {
                Ringtone ringtoneDefault = RingtoneManager.getRingtone(mContext, uriDefault);
                if (ringtoneDefault != null) {
                    sounds.put(ringtoneDefault.getTitle(mContext), uriDefault);
                }
            }
        }

        sounds.putAll(getSounds(mRingtoneType));

        final String[] titleArray = sounds.keySet().toArray(new String[0]);
        final Uri[] uriArray = sounds.values().toArray(new Uri[0]);

        int index = mValue != null ? Arrays.asList(uriArray).indexOf(Uri.parse(mValue)) : -1;

        builder.setSingleChoiceItems(titleArray, index, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (mRingtone != null) mRingtone.stop();

                Uri uri = uriArray[which];

                if (uri == null) {
                    mValue = null;
                    return;
                }

                if (uri.toString().length() > 0) {
                    mRingtone = RingtoneManager.getRingtone(mContext, uri);
                    if (mRingtone != null) mRingtone.play();
                }

                mValue = uri.toString();
            }
        });

        builder.setPositiveButton(R.string.action_save, this);
        builder.setNegativeButton(R.string.action_cancel, this);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        super.onDialogClosed(positiveResult);

        if (mRingtone != null)
            mRingtone.stop();

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

        if (mExtraRingtones != null && defaultValue != null && defaultValue.toString().length() > 0) {

            int index = Arrays.asList(mExtraRingtones).indexOf((CharSequence) defaultValue);

            mValue = (index >= 0) ?
                    uriFromRaw(defaultValue.toString()).toString() : (String)defaultValue;

        } else {
            mValue = (String)defaultValue;
        }

        persistString(mValue);
    }
}