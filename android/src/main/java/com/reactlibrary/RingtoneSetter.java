package com.reactlibrary;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RingtoneSetter {
	private ReactApplicationContext context;
	private ReadableMap settings;

	public RingtoneSetter(ReactApplicationContext context, ReadableMap settings) {
		this.context = context;
		this.settings = settings;
	}

	public Uri set() throws IOException, FileNotFoundException {
		File file = getFileFromPath();
		if (!file.exists()) throw new FileNotFoundException("File not found: " + settings.getString("filepath"));

		Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? getApi29RingtoneUri(file) : getRingtoneUri(file);
		boolean shouldSetAsDefault = settings.hasKey("isSetDefault") && settings.getBoolean("isSetDefault");
		if (shouldSetAsDefault) RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, uri);

		boolean usedCacheFile = FileUtils.isBundledResource(context, settings.getString("filepath"));
		if (usedCacheFile) file.delete();

		return uri;
	}

	private File getFileFromPath() throws IOException {
		String filePath = settings.getString("filepath");
		if (FileUtils.isBundledResource(context, filePath)) return FileUtils.bundledResourceToFile(context, filePath);
		return new File(filePath);
	}

	private Uri getApi29RingtoneUri(File file) throws IOException {
		ContentValues values = getContentValues(file);
		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
		FileUtils.copyFileToOutputStream(file, contentResolver.openOutputStream(uri));
		return uri;
	}

	private Uri getRingtoneUri(File file) throws IOException {
		String externalFilesPath = context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getPath();
		String name = settings.getString("title") + "." + MimeTypeMap.getFileExtensionFromUrl(settings.getString("filepath"));
		File externalFile = FileUtils.copyFile(file, externalFilesPath, name);

		ContentResolver contentResolver = context.getContentResolver();
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(externalFile.getAbsolutePath());
		contentResolver.delete(uri, MediaStore.MediaColumns.DATA + "=\"" + externalFile.getAbsolutePath() + "\"", null);
		Uri newUri = contentResolver.insert(uri, getContentValues(externalFile));

		return newUri;
	}

	private ContentValues getContentValues(File ringtoneFile) {
		ContentValues values = new ContentValues();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) values.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
		values.put(MediaStore.MediaColumns.TITLE, settings.getString("title"));
		values.put(MediaStore.MediaColumns.DISPLAY_NAME, settings.getString("title"));
		values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType());
		if (settings.hasKey("artist")) values.put(MediaStore.Audio.Media.ARTIST, settings.getString("artist"));
		if (settings.hasKey("isRingtone")) values.put(MediaStore.Audio.Media.IS_RINGTONE, settings.getBoolean("isRingtone"));
		if (settings.hasKey("isNotification")) values.put(MediaStore.Audio.Media.IS_NOTIFICATION, settings.getBoolean("isNotification"));
		if (settings.hasKey("isAlarm")) values.put(MediaStore.Audio.Media.IS_ALARM, settings.getBoolean("isAlarm"));
		if (settings.hasKey("isMusic")) values.put(MediaStore.Audio.Media.IS_MUSIC, settings.getBoolean("isMusic"));
		return values;
	}

	private String getMimeType() {
		if (settings.hasKey("mimeType")) return settings.getString("mimeType");
		String extension = MimeTypeMap.getFileExtensionFromUrl(settings.getString("filepath"));
		return extension == null ? null : MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
	}
}
