package com.vgelab.throwandcatch;

import android.app.AlertDialog;
import android.content.Context;

/**
 * 
 * @author shenshen
 * @email dslwz2008@gmail.com
 * @version 1.0
 * @comment 简单的警告窗体
 */
public class Alerts {
	/**
	 * 显示指定消息和Context的警告窗体
	 * @param message
	 * @param ctx
	 */
	public static void showDialog(String message, Context ctx) {
		// create a builder
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Alert window");

		// add buttons and listener
		DefaultOnClickListener el = new DefaultOnClickListener();
		builder.setPositiveButton("OK", el);
		builder.setMessage(message);

		// show
		AlertDialog ad = builder.create();
		ad.show();
	}
}
