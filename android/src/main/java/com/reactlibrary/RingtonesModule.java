package com.reactlibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RingtonesModule extends ReactContextBaseJavaModule {
    private final int writeSettingsRequestCode = 11111;

    public RingtonesModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Ringtones";
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode == this.writeSettingsRequestCode) {
            onPermissionResult();
        }
    }

    private boolean hasSettingsPermission() {
        boolean hasPermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = Settings.System.canWrite(getReactApplicationContext());
        }
        return hasPermission;
    }

    @ReactMethod
    public void hasPermission(Promise promise) {
        promise.resolve(hasSettingsPermission());
    }

    @ReactMethod
    private void requestSettingsPermission() {
        ReactApplicationContext application = getReactApplicationContext();
        if (!hasSettingsPermission()) {
            Intent intent = new Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:" + application.getPackageName())
            );
            application.startActivityForResult(intent, this.writeSettingsRequestCode, null);
        }
    }

    public void onPermissionResult() {
        WritableMap payload = new WritableNativeMap();
        payload.putBoolean("hasPermission", hasSettingsPermission());
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("writeSettingsPermission", payload);
    }

    @ReactMethod
    public void setNewRingtone(ReadableMap settings, Promise promise) {
        try {
            Activity activity = getCurrentActivity();
            if (activity == null) {
                promise.reject(new Exception("Null current activity"));
            } else {
                RingtoneSetter ringtoneSetter = new RingtoneSetter(getReactApplicationContext(), activity);
                Uri uri = ringtoneSetter.set(settings);

                WritableMap map = new WritableNativeMap();
                map.putString("title", settings.getString("title"));
                map.putString("uri", uri.toString());
                promise.resolve(map);
            }
        } catch (Throwable t) {
            promise.reject(t);
        }
    }

    @ReactMethod
    public void getRingtones(ReadableMap settings, Promise promise) {
        try {
            RingtoneManager ringtoneManager = new RingtoneManager(getCurrentActivity());
            if (settings.isNull("type")) {
                ringtoneManager.setType(RingtoneManager.TYPE_ALL);
            } else {
                ringtoneManager.setType(settings.getInt("type"));
            }
            ringtoneManager.setType(RingtoneManager.TYPE_ALL);
            Cursor cursor = ringtoneManager.getCursor();
            WritableArray list = new WritableNativeArray();
            while (cursor.moveToNext()) {
                WritableMap map = new WritableNativeMap();
                map.putString("title", cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                map.putString("uri", cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX));
                list.pushMap(map);
            }
            promise.resolve(list);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void setRingtone(ReadableMap settings, Promise promise) {
        try {
            Uri uri = Uri.parse(settings.getString("uri"));
            RingtoneManager.setActualDefaultRingtoneUri(getCurrentActivity(), RingtoneManager.TYPE_RINGTONE, uri);
            promise.resolve(true);
        } catch (Throwable t) {
            promise.reject(t);
        }
    }

    @ReactMethod
    public void deleteRingtone(ReadableMap settings, Promise promise) {
        try {
            Uri uri = Uri.parse(settings.getString("uri"));
            ContentResolver contentResolver = getCurrentActivity().getContentResolver();
            contentResolver.delete(uri, null, null);
            promise.resolve(true);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @ReactMethod
    public void getActualRingtone(Promise promise) {
        try {
            Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getCurrentActivity().getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            Ringtone defaultRingtone = RingtoneManager.getRingtone(getCurrentActivity(), defaultRingtoneUri);
            WritableMap map = new WritableNativeMap();
            map.putString("uri", defaultRingtoneUri.toString());
            map.putString("title", defaultRingtone.getTitle(getCurrentActivity().getApplicationContext()));
            promise.resolve(map);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }
}
