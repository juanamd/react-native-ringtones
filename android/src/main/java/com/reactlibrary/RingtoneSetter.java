package com.reactlibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.Ringtone;
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
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class RingtoneSetter {
	private ReactApplicationContext context;
	private ReadableMap settings;
	private Activity activity;

	public RingtoneSetter(ReactApplicationContext context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	public Uri set(ReadableMap settings) throws IOException, FileNotFoundException {
		this.settings = settings;

		String filePath = settings.getString("filepath");
		File cacheFile = isBundledResource(filePath) ? bundledToFile(filePath) : null;
		File file = cacheFile != null ? cacheFile : new File(filePath);
		if (!file.exists()) throw new FileNotFoundException("File not found: " + filePath);

		Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? getApi29RingtoneUri(file) : getRingtoneUri(file);
		if (settings.getBoolean("isSetDefault")) RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE, uri);
		if (cacheFile != null) cacheFile.delete();

		return uri;
	}

	private boolean isBundledResource(String fileName) {
		int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
		return resId != 0;
	}

    private File bundledToFile(String fileName) throws IOException {
        File file = new File(context.getCacheDir() + File.separator + fileName);
        int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        InputStream inputStream = context.getResources().openRawResource(resId);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;

        while((len = inputStream.read(buf)) > 0) {
            fileOutputStream.write(buf, 0, len);
        }
        fileOutputStream.close();
        inputStream.close();

        return file;
    }

	private Uri getApi29RingtoneUri(File file) throws IOException {
		ContentValues values = getContentValues(file);
		ContentResolver contentResolver = activity.getContentResolver();
		Uri uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
		OutputStream os = contentResolver.openOutputStream(uri);
		int size = (int) file.length();
		byte[] bytes = new byte[size];

		BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
		buf.read(bytes, 0, bytes.length);
		buf.close();
		os.write(bytes);
		os.close();
		os.flush();

		return uri;
	}

	private Uri getRingtoneUri(File file) throws IOException {
		String externalFilesPath = context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getPath();
		String name = settings.getString("title") + "." + MimeTypeMap.getFileExtensionFromUrl(settings.getString("filepath"));
		File externalFile = exportFile(file, new File(externalFilesPath), name);

		ContentResolver contentResolver = activity.getContentResolver();
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(externalFile.getAbsolutePath());
		contentResolver.delete(uri, MediaStore.MediaColumns.DATA + "=\"" + externalFile.getAbsolutePath() + "\"", null);
		Uri newUri = contentResolver.insert(uri, getContentValues(externalFile));

		return newUri;
	}

    private File exportFile(File src, File dst, String name) throws IOException {
        if (!dst.exists() && !dst.mkdir()) throw new IOException("Can't create destination file directory");

        File expFile = new File(dst.getPath() + File.separator + name);
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(expFile).getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        if (inChannel != null) inChannel.close();
        if (outChannel != null) outChannel.close();

        return expFile;
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
