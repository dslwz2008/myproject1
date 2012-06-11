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
	 *  �ж�SD���Ƿ����
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
	 *  �����ļ���
	 * @param filename
	 * @return �Ƿ񴴽��ɹ�
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
	 * @return �����ļ���չ��
	 */
	public static String getExtension(File f) {
		return (f != null) ? getExtension(f.getName()) : "";
	}

	/**
	 * ����ļ���չ��
	 * @param filename
	 * @return �����ļ���չ��
	 */
	public static String getExtension(String filename) {
		return getExtension(filename, "");
	}

	/**
	 * ����ļ���չ��
	 * @param filename
	 * @param defExt Ĭ����չ��
	 * @return �����ļ���չ��
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
	 * ȥ����չ��
	 * @param filename
	 * @return ����ȥ����չ������ļ���
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
