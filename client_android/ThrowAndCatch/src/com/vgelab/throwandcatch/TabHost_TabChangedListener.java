package com.vgelab.throwandcatch;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * 
 * @author : shenshen
 * @email : dslwz2008@gmail.com
 * @version : 1.0
 * @create ：2012-6-6 上午11:55:46
 * @comment : TabHost 中Tab发生变化的响应类
 *
 */
public class TabHost_TabChangedListener implements TabHost.OnTabChangeListener {

	private ThrowAndCatchActivity m_activity;

	/**
	 * 构造函数
	 * @param activity
	 */
	public TabHost_TabChangedListener(Activity activity) {
		m_activity = (ThrowAndCatchActivity) activity;
	}

	/**
	 * Tab发生变化的响应函数
	 * @param 当前的ID(变化后的)
	 */
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if (tabId == m_activity.getString(R.string.tab_video)) {
			selectTabItem(3);
			setToolboxVisiable(false);
		} else if (tabId == m_activity.getString(R.string.tab_image)) {
			selectTabItem(2);
			setToolboxVisiable(false);
		} else if (tabId == m_activity.getString(R.string.tab_files)) {
			m_activity.listDeviceDataDirectory();
			selectTabItem(0);
			setToolboxVisiable(true);
		} else if (tabId == m_activity.getString(R.string.tab_server)) {
			m_activity.listServerDataDirectory();
			selectTabItem(1);
			setToolboxVisiable(true);
		} else {
			// impossible to reach
			return;
		}
	}
	
	/**
	 * 选择指定的Tab标签
	 * @param index
	 */
	private void selectTabItem(int index)
	{
		TabHost tabHost = (TabHost)m_activity.findViewById(R.id.tabhost);
		TabWidget tabWidget = tabHost.getTabWidget();
		for(int i=0; i<tabWidget.getChildCount(); i++)
		{
			View view = (View)tabWidget.getChildAt(i);
			Drawable drawable = m_activity.getResources().getDrawable(
						i == index ? R.drawable.bg_tabitem_selected : R.drawable.bg_tabitem);
			view.setBackgroundDrawable(drawable);
		}
	}
	
	/**
	 * 设置工具栏是否可见
	 * @param visiable
	 */
	private void setToolboxVisiable(boolean visiable)
	{
		LinearLayout toolBox = (LinearLayout)m_activity.findViewById(R.id.toolBox);
		toolBox.setVisibility(visiable?View.VISIBLE:View.INVISIBLE);
	}
}
