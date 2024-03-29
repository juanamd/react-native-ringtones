package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
		if (requestCode != this.writeSettingsRequestCode) return;
		try {
			WritableMap payload = new WritableNativeMap();
			payload.putBoolean("hasPermission", hasSettingsPermission());
			getReactApplicationContext()
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit("writeSettingsPermission", payload);
		} catch (Exception ex) {}
	}

	@ReactMethod
	public void hasSettingsPermission(Promise promise) {
		try {
			boolean hasPermission = hasSettingsPermission();
			promise.resolve(hasPermission);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	private boolean hasSettingsPermission() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
		return Settings.System.canWrite(getReactApplicationContext());
	}

	@ReactMethod
	private void requestSettingsPermission(Promise promise) {
		try {
			boolean hasPermission = hasSettingsPermission();
			if (!hasPermission) startSettingsPermissionActivity();
			promise.resolve(!hasPermission);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	private void startSettingsPermissionActivity() {
		ReactApplicationContext context = getReactApplicationContext();
		Uri uri = Uri.parse("package:" + context.getPackageName());
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, uri);
		context.startActivityForResult(intent, this.writeSettingsRequestCode, null);
	}

	@ReactMethod
	public void getRingtones(int type, Promise promise) {
		try {
			RingtoneManager ringtoneManager = new RingtoneManager(getReactApplicationContext());
			ringtoneManager.setType(type);
			Cursor cursor = ringtoneManager.getCursor();
			WritableArray list = new WritableNativeArray();
			cursor.moveToPosition(-1);
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
	public void getMediaStoreRingtones(Promise promise) {
		try {
			Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE };
			ContentResolver contentResolver = getReactApplicationContext().getContentResolver();
			Cursor cursor = contentResolver.query(uri, projection, null, null, null);
			WritableArray list = new WritableNativeArray();
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				WritableMap map = new WritableNativeMap();
				long contentId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
				map.putString("title", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
				map.putString("uri", ContentUris.withAppendedId(uri, contentId).toString());
				list.pushMap(map);
			}
			promise.resolve(list);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void setNewRingtone(ReadableMap settings, Promise promise) {
		try {
			RingtoneSetter ringtoneSetter = new RingtoneSetter(getReactApplicationContext(), settings);
			Uri uri = ringtoneSetter.set();

			WritableMap map = new WritableNativeMap();
			map.putString("title", settings.getString("title"));
			map.putString("uri", uri.toString());
			promise.resolve(map);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void setRingtone(String uriString, int type, Promise promise) {
		try {
			Uri uri = uriString == null ? null : Uri.parse(uriString);
			RingtoneManager.setActualDefaultRingtoneUri(getReactApplicationContext(), type, uri);
			promise.resolve(null);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void deleteRingtone(String uriString, Promise promise) {
		try {
			Uri uri = Uri.parse(uriString);
			ContentResolver contentResolver = getReactApplicationContext().getContentResolver();
			int deletedRows = contentResolver.delete(uri, null, null);
			promise.resolve(deletedRows);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void getActualRingtone(int type, Promise promise) {
		try {
			Context context = getReactApplicationContext();
			Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, type);
			Ringtone defaultRingtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
			WritableMap map = new WritableNativeMap();
			map.putString("uri", defaultRingtoneUri != null ? defaultRingtoneUri.toString() : null);
			map.putString("title", defaultRingtone != null ? defaultRingtone.getTitle(context) : null);
			promise.resolve(map);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}
}
