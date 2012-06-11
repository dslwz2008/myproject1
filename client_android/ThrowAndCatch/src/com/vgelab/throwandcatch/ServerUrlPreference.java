package com.vgelab.throwandcatch;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * Url配置
 */
public class ServerUrlPreference extends EditTextPreference {

	/**
	 * 重写基类的构造
	 * @param context
	 */
	public ServerUrlPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 重写基类的构造
	 * @param context
	 * @param attrs
	 */
	public ServerUrlPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * 重写基类的构造
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ServerUrlPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	/**
	 * 输入窗口关闭时调用
	 */
	protected void onDialogClosed(boolean positiveResult)
	{
		if(!positiveResult)
		{
			super.onDialogClosed(false);
			return;
		}
		
		String url = getEditText().getText().toString();
		if(positiveResult)
		{
			if (Internet.test("http://" + url + ":10002/Test/")) {
				super.onDialogClosed(true);
				Toast.makeText(this.getContext(), "设置了服务器地址： " + url, Toast.LENGTH_LONG)
						.show();
			}
			else {
				super.onDialogClosed(false);
				Toast.makeText(this.getContext(), "连接服务器失败： " + url, Toast.LENGTH_LONG)
						.show();
			}
			
		}
	}
}
