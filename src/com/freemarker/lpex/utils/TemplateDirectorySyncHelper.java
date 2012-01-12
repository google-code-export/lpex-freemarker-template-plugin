package com.freemarker.lpex.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.freemarker.lpex.Activator;
import com.freemarker.lpex.preferences.PreferenceConstants;

public class TemplateDirectorySyncHelper {

	static private final int FAT_PRECISION = 2000;
	static public final long DEFAULT_COPY_BUFFER_SIZE = 16 * 1024 * 1024; // 16 MB
	static private String syncDirectoryPath;
	static private boolean syncEnabled;

	public static void init(boolean sync, String syncDirectoryPath) {
		setSync(sync);
		setSyncDirectoryPath(syncDirectoryPath);
	}

	public static String getSyncDirectoryPath() {
		return syncDirectoryPath;
	}

	public static void setSyncDirectoryPath(String syncDirectoryPath) {
		TemplateDirectorySyncHelper.syncDirectoryPath = syncDirectoryPath;
	}
	
	public static boolean isSyncEnabled() {
		return syncEnabled;
	}

	public static void enableSync() {
		TemplateDirectorySyncHelper.syncEnabled = true;
	}

	public static void disableSync() {
		TemplateDirectorySyncHelper.syncEnabled = false;
	}

	public static void setSync(boolean syncOption) {
		TemplateDirectorySyncHelper.syncEnabled = syncOption;
	}

	public static void syncLocalTemplates() {
		if (!syncEnabled){
			return;
		}
		
		String baseTemplateFolder = "";
		try {
			baseTemplateFolder = Activator.preferenceStore.getString(PreferenceConstants.P_TEMPLATES_DIR);
			if (!new File(baseTemplateFolder).exists())
			{
				PluginLogger.logger.warning("No template directory set");
				return;
			}
		} catch (Exception e) {
			PluginLogger.logger.warning(StackTraceUtil.getStackTrace(e));
			return;
		}
		if (baseTemplateFolder == "") {
			PluginLogger.logger.warning("No template directory set");
	        return;
		}
		
		String syncTemplateFolder = "";
		try {
			syncTemplateFolder = Activator.preferenceStore.getString(PreferenceConstants.P_TEMPLATES_SYNC_DIR);
			if (!new File(syncTemplateFolder).exists())
			{
			    PluginLogger.logger.warning("No sync template directory set");
			    return;
			}
		} catch (Exception e) {
			PluginLogger.logger.warning(StackTraceUtil.getStackTrace(e));
			PluginLogger.logger.warning("No sync template directory set");
			return;
		}
		if (syncTemplateFolder == "") {
			PluginLogger.logger.warning("No sync template directory set");
	        return;
		}
		
		//Sync the files in the sync directory down to the local directory
		// replacing any with the same name that exists but do not disturb
		// any files that were created in the local directory that are not
		// found in the sync directory.
		File templateDirectory = new File(baseTemplateFolder);
		File templateSyncDirectory = new File(syncTemplateFolder);
		
		if ( !templateDirectory.exists() ) {
			PluginLogger.logger.warning("Local template directory does not exist.");
			return;
	    } else if ( !templateDirectory.isDirectory() ) {
			PluginLogger.logger.warning("Local template path is not a directory.");
			return;
        }
		
		try {
			synchronize(templateSyncDirectory, templateDirectory, false);
		} catch (IOException e) {
			PluginLogger.logger.warning("Synchronization failed.");
			PluginLogger.logger.warning(StackTraceUtil.getStackTrace(e));
		}
	    
		return;
	}

	public static boolean areInSync(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			if (!destination.exists()) {
				return false;
			} else if (!destination.isDirectory()) {
				throw new IOException(
						"Source and Destination not of the same type:"
								+ source.getCanonicalPath() + " , "
								+ destination.getCanonicalPath());
			}
			String[] sources = source.list();
			boolean inSync = true;
			for (String fileName : sources) {
				File srcFile = new File(source, fileName);
				File destFile = new File(destination, fileName);
				if (!areInSync(srcFile, destFile)) {
					inSync = false;
					break;
				}
			}
			return inSync;
		} else {
			if (destination.exists() && destination.isFile()) {
				long sts = source.lastModified() / FAT_PRECISION;
				long dts = destination.lastModified() / FAT_PRECISION;
				return sts == dts;
			} else {
				return false;
			}
		}
	}

	public static void synchronize(File source, File destination, boolean smart) throws IOException {
		synchronize(source, destination, smart, DEFAULT_COPY_BUFFER_SIZE);
	}

	public static void synchronize(File source, File destination,
			boolean smart, long chunkSize) throws IOException {
		if (chunkSize <= 0) {
			System.out.println("Chunk size must be positive: using default value.");
			chunkSize = DEFAULT_COPY_BUFFER_SIZE;
		}
		if (source.isDirectory()) {
			if (!destination.exists()) {
				if (!destination.mkdirs()) {
					throw new IOException("Could not create path "
							+ destination);
				}
			} else if (!destination.isDirectory()) {
				throw new IOException(
						"Source and Destination not of the same type:"
								+ source.getCanonicalPath() + " , "
								+ destination.getCanonicalPath());
			}
			String[] sources = source.list();
			Set<String> srcNames = new HashSet<String>(Arrays.asList(sources));
			String[] dests = destination.list();

			// copy each file from source
			for (String fileName : sources) {
				File srcFile = new File(source, fileName);
				File destFile = new File(destination, fileName);
				synchronize(srcFile, destFile, smart, chunkSize);
			}
		} else {
			if (destination.exists() && destination.isDirectory()) {
				delete(destination);
			}
			if (destination.exists()) {
				long sts = source.lastModified() / FAT_PRECISION;
				long dts = destination.lastModified() / FAT_PRECISION;
				// do not copy if smart and same timestamp and same length
				if (!smart || sts == 0 || sts != dts
						|| source.length() != destination.length()) {
					copyFile(source, destination, chunkSize);
				}
			} else {
				copyFile(source, destination, chunkSize);
			}
		}
	}

	private static void copyFile(File srcFile, File destFile, long chunkSize) throws IOException {
		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			is = new FileInputStream(srcFile);
			FileChannel iChannel = is.getChannel();
			os = new FileOutputStream(destFile, false);
			FileChannel oChannel = os.getChannel();
			long doneBytes = 0L;
			long todoBytes = srcFile.length();
			while (todoBytes != 0L) {
				long iterationBytes = Math.min(todoBytes, chunkSize);
				long transferredLength = oChannel.transferFrom(iChannel,
						doneBytes, iterationBytes);
				if (iterationBytes != transferredLength) {
					throw new IOException(
							"Error during file transfer: expected "
									+ iterationBytes + " bytes, only "
									+ transferredLength + " bytes copied.");
				}
				doneBytes += transferredLength;
				todoBytes -= transferredLength;
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
		boolean successTimestampOp = destFile.setLastModified(srcFile
				.lastModified());
		if (!successTimestampOp) {
			System.out.println("Could not change timestamp for {}. Index synchronization may be slow. "
							+ destFile);
		}
	}

	public static void delete(File file) {
		if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				delete(subFile);
			}
		}
		if (file.exists()) {
			if (!file.delete()) {
				System.out.println("Could not delete {}" + file);
			}
		}
	}
	
}
