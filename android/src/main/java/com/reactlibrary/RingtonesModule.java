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
			RingtoneSetter ringtoneSetter = new RingtoneSetter(getReactApplicationContext(), settings);
			Uri uri = ringtoneSetter.set();

			WritableMap map = new WritableNativeMap();
			map.putString("title", settings.getString("title"));
			map.putString("uri", uri.toString());
			promise.resolve(map);
		} catch (Throwable t) {
			promise.reject(t);
		}
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
	public void setRingtone(ReadableMap settings, Promise promise) {
		try {
			String uriString = settings.getString("uri");
			Uri uri = uriString != null ? Uri.parse(uriString) : null;
			RingtoneManager.setActualDefaultRingtoneUri(getReactApplicationContext(), RingtoneManager.TYPE_RINGTONE, uri);
			promise.resolve(true);
		} catch (Throwable t) {
			promise.reject(t);
		}
	}

	@ReactMethod
	public void deleteRingtone(ReadableMap settings, Promise promise) {
		try {
			Uri uri = Uri.parse(settings.getString("uri"));
			ContentResolver contentResolver = getReactApplicationContext().getContentResolver();
			contentResolver.delete(uri, null, null);
			promise.resolve(true);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}

	@ReactMethod
	public void getActualRingtone(Promise promise) {
		try {
			Context context = getReactApplicationContext();
			Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
			Ringtone defaultRingtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
			WritableMap map = new WritableNativeMap();
			map.putString("uri", defaultRingtoneUri != null ? defaultRingtoneUri.toString() : null);
			map.putString("title", defaultRingtone != null ? defaultRingtone.getTitle(context): null);
			promise.resolve(map);
		} catch (Exception ex) {
			promise.reject(ex);
		}
	}
}
