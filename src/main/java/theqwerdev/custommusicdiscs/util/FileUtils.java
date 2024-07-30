package theqwerdev.custommusicdiscs.util;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
	public static File fileSelectionPrompt(String dialogTitle, FileFilter filter) {
		File file = null;

		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int dialogResult = fileChooser.showOpenDialog(null);
		if(dialogResult == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		}

		return file;
	}

	public static boolean deleteDirectory(File dir) {
		File[] files = dir.listFiles();

		if(files != null)
			for(File file : files)
				deleteDirectory(file);

		return dir.delete();
	}

	public static File fileFromZip(File destDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destDir, zipEntry.getName());

		String destDirPath = destDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if(!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public static void zipFile(File fileToZip, String filename, ZipOutputStream zipOut) throws IOException {

		if(fileToZip.isDirectory()) {
			if(filename.endsWith("/"))
				zipOut.putNextEntry(new ZipEntry(filename));
			else
				zipOut.putNextEntry(new ZipEntry(filename + '/'));
			zipOut.closeEntry();

			File[] children = fileToZip.listFiles();
			if(children != null) {
				for(File child : children) {
					zipFile(child, filename + '/' + child.getName(), zipOut);
				}
			}

			return;
		}

		ZipEntry zipEntry = new ZipEntry(filename);
		zipOut.putNextEntry(zipEntry);
		byte[] buffer = Files.readAllBytes(fileToZip.toPath());
		zipOut.write(buffer, 0, buffer.length);
		zipOut.closeEntry();
	}

	public static void unzipFile(File zipFile) throws IOException {
		ZipInputStream inputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()));
		ZipEntry zipEntry = inputStream.getNextEntry();

		while(zipEntry != null) {
			File file = fileFromZip(new File("."), zipEntry);

			if(zipEntry.isDirectory()) {
				if(!file.isDirectory() && !file.mkdirs())
					throw new IOException("Failed to create directory " + file);
			}
			else {
				File parent = file.getParentFile();
				if(!parent.isDirectory() && !parent.mkdirs())
					throw new IOException("Failed to create directory " + parent);

				FileOutputStream outputStream = new FileOutputStream(file);
				byte[] buffer = new byte[4096];
				int len;
				while((len = inputStream.read(buffer)) != -1)
					outputStream.write(buffer, 0 , len);
				outputStream.close();
			}
			zipEntry = inputStream.getNextEntry();
		}

		inputStream.closeEntry();
		inputStream.close();
	}

}
