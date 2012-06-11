package com.vgelab.throwandcatch;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * Url����
 */
public class ServerUrlPreference extends EditTextPreference {

	/**
	 * ��д����Ĺ���
	 * @param context
	 */
	public ServerUrlPreference(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * ��д����Ĺ���
	 * @param context
	 * @param attrs
	 */
	public ServerUrlPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * ��д����Ĺ���
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ServerUrlPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	/**
	 * ���봰�ڹر�ʱ����
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
				Toast.makeText(this.getContext(), "�����˷�������ַ�� " + url, Toast.LENGTH_LONG)
						.show();
			}
			else {
				super.onDialogClosed(false);
				Toast.makeText(this.getContext(), "���ӷ�����ʧ�ܣ� " + url, Toast.LENGTH_LONG)
						.show();
			}
			
		}
	}
}
