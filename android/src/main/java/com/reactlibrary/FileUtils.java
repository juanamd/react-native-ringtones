package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {
	public static boolean isBundledResource(ReactApplicationContext context, String fileName) {
		int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
		return resId != 0;
	}

	public static File bundledResourceToFile(ReactApplicationContext context, String fileName) throws IOException {
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

	public static File copyFile(File src, String dstPath, String name) throws IOException {
		File dstDir = new File(dstPath);
		if (!dstDir.exists() && !dstDir.mkdir()) throw new IOException("Can't create destination file directory");

		File expFile = new File(dstDir.getPath() + File.separator + name);
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(expFile).getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		if (inChannel != null) inChannel.close();
		if (outChannel != null) outChannel.close();

		return expFile;
	}

	public static void copyFileToOutputStream(File file, OutputStream os) throws IOException {
		int size = (int) file.length();
		byte[] bytes = new byte[size];

		BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
		buf.read(bytes, 0, bytes.length);
		buf.close();
		os.write(bytes);
		os.close();
		os.flush();
	}
}
