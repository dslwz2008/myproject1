package com.vgelab.throwandcatch;

import java.io.File;

/**
 * @author shenshen
 * @email dslwz2008@gmail.com
 * @version 1.0
 * 
 */
public class CommonUtility {
	/**
	 *  判断SD卡是否存在
	 * @return
	 */
	public static boolean hasSdcard() {
		String status = android.os.Environment.getExternalStorageState();
		if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  创建文件夹
	 * @param filename
	 * @return 是否创建成功
	 */
	public static boolean makeDirectory(String filename) {
		File destDir = new File(filename);
		if (!destDir.exists()) {
			return destDir.mkdirs();
		}
		return false;
	}

	/**
	 *  Return the extension portion of the file's name .
	 * @param f
	 * @return 返回文件扩展名
	 */
	public static String getExtension(File f) {
		return (f != null) ? getExtension(f.getName()) : "";
	}

	/**
	 * 获得文件扩展名
	 * @param filename
	 * @return 返回文件扩展名
	 */
	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	/**
	 * 获得文件扩展名
	 * @param filename
	 * @param defExt 默认扩展名
	 * @return 返回文件扩展名
	 */
	public static String getExtension(String filename, String defExt) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');

			if ((i > -1) && (i < (filename.length() - 1))) {
				return filename.substring(i + 1);
			}
		}
		return defExt;
	}

	/**
	 * 去掉扩展名
	 * @param filename
	 * @return 返回去掉扩展名后的文件名
	 */
	public static String trimExtension(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int i = filename.lastIndexOf('.');
			if ((i > -1) && (i < (filename.length()))) {
				return filename.substring(0, i);
			}
		}
		return filename;
	}
}
