package com.vgelab.throwandcatch;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * 
 * @author shenshen
 * @email dslwz2008@gmail.com
 * @version 1.0
 *
 */
public class AccelerometerEventListener implements SensorEventListener {

	private ThrowAndCatchActivity m_activity;
	private float[] gravity = new float[3];
	private float[] motion = new float[3];

	long lastUpdate, lastActionTime = 0;
	float x = 0, y = 0, last_x = 0, last_y = 0;
	final int ACTION_INTERVAL = 2000;
	final int UPDATE_THRESHOLD = 50;
	final float ACCELERO_THRESHOLD = 30.0f;

	/**
	 * 构造函数
	 * @param activity
	 */
	public AccelerometerEventListener(Activity activity) {
		m_activity = (ThrowAndCatchActivity) activity;
	}

	/**
	 * 精确变化，暂时不用
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	/**
	 * 传感器变化
	 * @param 传感器事件
	 */
	public void onSensorChanged(SensorEvent event) {
		final float alpha = 0.8f;
		for (int i = 0; i < 3; i++) {
			gravity[i] = (float) ((1 - alpha) * event.values[i] + alpha
					* gravity[i]);
			motion[i] = event.values[i] - gravity[i];
		}

		long curTime = System.currentTimeMillis();
		// detect per 100 milliseconds
		if ((curTime - lastUpdate) > UPDATE_THRESHOLD) {
			lastUpdate = curTime;
			// 先不用z的数据
			x = motion[SensorManager.DATA_X];
			y = motion[SensorManager.DATA_Y];
			// z = Math.abs(values[SensorManager.DATA_Z]);
			float deltaX = Math.abs(x - last_x + y - last_y);
			// float deltaY = Math.abs(y - last_y);
			if (curTime - lastActionTime > ACTION_INTERVAL
					&& deltaX > ACCELERO_THRESHOLD) {
				TabHost tabhost = (TabHost) m_activity
						.findViewById(R.id.tabhost);
				int tabid = tabhost.getCurrentTab();
				if (tabid == 0) {// 必须是在本地文件夹浏览的情况下才能上传
					m_activity.SendFiles();
				} else if (tabid == 1) {// 必须是在服务端文件夹浏览的情况下才能下载
					m_activity.ReceiveFiles();
				} else {
					Toast.makeText(m_activity,
							m_activity.getString(R.string.error_send_mode),
							Toast.LENGTH_SHORT).show();
				}
				lastActionTime = curTime;
			}

			last_x = x;
			last_y = y;
		}

	}
	// 6.5之前的版本
	// public void onSensorChanged(SensorEvent event) {
	// final float alpha = 0.8f;
	// for (int i = 0; i < 3; i++) {
	// gravity[i] = (float) ((1 - alpha) * event.values[i] + alpha
	// * gravity[i]);
	// motion[i] = event.values[i] - gravity[i];
	// }
	//
	// // String strMotion = String.format(
	// // " X: %5.2f Y: %5.2f",
	// // motion[0], motion[1]);
	// // Log.v("data", strMotion);
	//
	// long curTime = System.currentTimeMillis();
	// // detect per 100 milliseconds
	// if ((curTime - lastUpdate) > UPDATE_THRESHOLD) {
	// lastUpdate = curTime;
	// // 先不用z的数据
	// x = motion[SensorManager.DATA_X];
	// y = motion[SensorManager.DATA_Y];
	// // z = Math.abs(values[SensorManager.DATA_Z]);
	// float deltaX = Math.abs(x - last_x);
	// // float deltaY = Math.abs(y - last_y);
	// if (curTime - lastActionTime > ACTION_INTERVAL
	// // && (deltaX > ACCELERO_THRESHOLD || deltaY > ACCELERO_THRESHOLD))
	// // {
	// && deltaX > ACCELERO_THRESHOLD) {
	// // 找到此次动作的主导方向
	// // 目前先对X轴的动作进行识别
	// boolean xAxis = true;// deltaX > deltaY ? true : false;
	// if (xAxis) {
	// // 向右的动作,上传文件！
	// if (x < 0.f && last_x > 0.f) {
	// // String strMotion = String.format(
	// // "R: X: %5.2f Y: %5.2f\tlast X: %5.2f Y: %5.2f",
	// // motion[0], motion[1], last_x, last_y);
	// // Toast.makeText(m_activity, strMotion,
	// // Toast.LENGTH_SHORT).show();
	// // Log.v("action Right", strMotion);\
	// TabHost tabhost = (TabHost) m_activity
	// .findViewById(R.id.tabhost);
	// int tabid = tabhost.getCurrentTab();
	// if (tabid == 0) {// 必须是在本地文件夹浏览的情况下才能上传
	// m_activity.SendFiles();
	// } else {
	// Toast.makeText(
	// m_activity,
	// m_activity
	// .getString(R.string.error_send_mode),
	// Toast.LENGTH_SHORT).show();
	// }
	// lastActionTime = curTime;
	// } else if (x > 0.f && last_x < 0.f) {// left，下载文件！
	// // String strMotion = String.format(
	// // "L: X: %5.2f Y: %5.2f\tlast X: %5.2f Y: %5.2f",
	// // motion[0], motion[1], last_x, last_y);
	// // Toast.makeText(m_activity, strMotion,
	// // Toast.LENGTH_SHORT).show();
	// // Log.v("action Left", strMotion);
	// TabHost tabhost = (TabHost) m_activity
	// .findViewById(R.id.tabhost);
	// int tabid = tabhost.getCurrentTab();
	// if (tabid == 1) {// 必须是在服务端文件夹浏览的情况下才能下载
	// m_activity.ReceiveFiles();
	// } else {
	// Toast.makeText(
	// m_activity,
	// m_activity
	// .getString(R.string.error_receive_mode),
	// Toast.LENGTH_SHORT).show();
	// }
	// lastActionTime = curTime;
	// } else {
	// // ignore
	// }
	// } else {
	// // if (y > 0.f && last_y > 0.f) {
	// // String strMotion = String.format(
	// // "U: X: %5.2f Y: %5.2f\tlast X: %5.2f Y: %5.2f",
	// // motion[0], motion[1], last_x, last_y);
	// // Toast.makeText(MainActivity.this, strMotion,
	// // Toast.LENGTH_SHORT).show();
	// // Log.v("action Up", strMotion);
	// // lastShakeTime = curTime;
	// // } else if (y < 0.f && last_y < 0.f) {// left
	// // String strMotion = String.format(
	// // "D: X: %5.2f Y: %5.2f\tlast X: %5.2f Y: %5.2f",
	// // motion[0], motion[1], last_x, last_y);
	// // Toast.makeText(MainActivity.this, strMotion,
	// // Toast.LENGTH_SHORT).show();
	// // Log.v("action Down", strMotion);
	// // lastShakeTime = curTime;
	// // } else {
	// // // ignore
	// // }
	// }
	// }
	//
	// last_x = x;
	// last_y = y;
	// }
	//
	// }
}
