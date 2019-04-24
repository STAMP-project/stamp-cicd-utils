package eu.stamp_project.cicd.utils.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileUtils {

	/**
	 * Delete file or directory (empty or not)
	 * @param file The file or directory to delete
	 * @throws IOException
	 */
	public static void deleteIfExists(File file) throws IOException {
		if(file == null || ! file.exists()) return;
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			if (entries != null) {
				for (File entry : entries) {
					deleteIfExists(entry);
				}
			}
		}
		if (!file.delete()) {
			throw new IOException("Failed to delete " + file);
		}
	}

	/**
	 * Write data to temporary file, and return path
	 * @param data The data to write
	 * @return A temporary file absolute path
	 * @throws IOException
	 */
	public static String tempFile(String data) throws IOException {
		File temp = File.createTempFile("stamp", null);
		temp.deleteOnExit();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(temp));
			out.print(data);
		} catch(IOException e) {
			throw(e);
		} finally {
			if(out != null) out.close();
		}
		return temp.getAbsolutePath();
	}
}
