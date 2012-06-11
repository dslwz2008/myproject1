package com.vgelab.throwandcatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * @author : shenshen
 * @email : dslwz2008@gmail.com
 * @version : 1.0
 * @create ：2012-6-6 上午11:09:45
 * @comment : 自定义的Preference,用来设置设备上的数据路径
 */
public class FileDialogPreference extends Preference {

	private Button m_button;
	private Context m_context;

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 */
	public FileDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
		setLayoutResource(R.layout.directory_dialog);
	}

	/**
	 * 构造函数
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FileDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		m_context = context;
	}
	
	/**
	 * 构造函数
	 * @param context
	 */
	public FileDialogPreference(Context context){
		super(context);
		m_context = context;
	}

	/**
	 * 重载绑定View的函数，实现自定义的View
	 */
	@Override
	protected void onBindView(View view) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(m_context);
		String textBtn = sp.getString(m_context.getString(R.string.str_local_data_path),
				m_context.getString(R.string.lbl_select_dir));
		m_button = (Button) view.findViewById(R.id.btn_gotoselect);
		m_button.setText(textBtn);
		m_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(m_context, FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, m_context.getString(R.string.str_start_path));
				// can user select directories or not
				intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
				// alternatively you can set file filter
				intent.putExtra(FileDialog.FORMAT_FILTER,
						new String[] { m_context.getString(R.string.pic_JPEG),
								m_context.getString(R.string.pic_Bitmap),
								m_context.getString(R.string.pic_GIF),
								m_context.getString(R.string.pic_PNG),
								m_context.getString(R.string.vid_3GPP),
								m_context.getString(R.string.vid_MPEG_4) });
				Activity activity = (Activity) v.getContext();
				//Toast.makeText(activity, activity.toString(), Toast.LENGTH_SHORT).show();
				activity.startActivityForResult(intent, RequestCode.SELECT_DIR_REQUEST);
			}
		});

		super.onBindView(view);
	}

}
