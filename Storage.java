package com.schindler.schindler.tag;

import android.annotation.SuppressLint;

import com.schindler.commnonclass.ConstantData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressLint("SimpleDateFormat") public class Storage {

	public static void verifyLogPath() throws IOException {
		File dir = new File(ConstantData.LOG_DIR);

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		dir = null;
	}

	public static void verifyDataPath() throws IOException {

		File dir = new File(ConstantData.LOG_IMAGE);

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		dir = null;
	}

	public static File verifyLogFile() throws IOException {
		File logFile = new File(ConstantData.LOG_DIR + "/Log_" + new SimpleDateFormat("yyyy_MM_dd").format(new Date()) + ".html");
		FileOutputStream fos = null;

		Storage.verifyLogPath();

		if (!logFile.exists())
		{
			logFile.createNewFile();

			fos = new FileOutputStream(logFile);

			String str = "<TABLE style=\"width:100%;border=1px\" cellpadding=2 cellspacing=2 border=1><TR>" + "<TD style=\"width:30%\"><B>Date n Time</B></TD>"
					+ "<TD style=\"width:20%\"><B>Title</B></TD>" + "<TD style=\"width:50%\"><B>Description</B></TD></TR>";

			fos.write(str.getBytes());
		}

		if (fos != null)
		{
			fos.close();
		}

		fos = null;

		return logFile;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try
		{
			byte[] bytes = new byte[buffer_size];
			for (;;)
			{
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch (Exception ex)
		{
		}
	}

	public static void createLogZip() {
		ZipOutputStream zout = null;
		FileInputStream fis = null;
		String files[] = null;
		int ch;
		try
		{
			files = new File(ConstantData.LOG_DIR).list();

			zout = new ZipOutputStream(new FileOutputStream(ConstantData.LOG_ZIP));

			zout.setLevel(Deflater.DEFAULT_COMPRESSION);

			for (int ele = 0; ele < files.length; ele++)
			{
				fis = new FileInputStream(ConstantData.LOG_DIR + "/" + files[ele]);

				
				zout.putNextEntry(new ZipEntry(ConstantData.LOG_DIR + "/" + files[ele]));

				

				while ((ch = fis.read()) > 0)
				{
					zout.write(ch);
				}

				

				zout.closeEntry();

				fis.close();
			}

			zout.close();

		}
		catch (Exception e)
		{
			Log.error(Storage.class + " :: create log zip :: ", e);
		}

		zout = null;
		fis = null;
		files = null;
	}

	public static void clearLog() {
		String files[] = null;
		File file = null;
		try
		{
			files = new File(ConstantData.LOG_DIR).list();

			for (int ele = 0; ele < files.length; ele++)
			{
				file = new File(ConstantData.LOG_DIR, files[ele]);

				file.delete();
			}

		}
		catch (Exception e)
		{
			Log.error(Storage.class + " :: clearLog :: ", e);
		}

		files = null;
		files = null;
	}

	public static void delete(String f) throws IOException {
		File file = new File(f);

		if (file.exists())
		{
			file.delete();
		}

		file = null;
		f = null;
	}

}
