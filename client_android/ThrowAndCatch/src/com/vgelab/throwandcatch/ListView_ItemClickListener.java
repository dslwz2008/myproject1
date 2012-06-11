package com.vgelab.throwandcatch;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * @author shenshen
 * @email dslwz2008@gmail.com
 * @version 1.0
 * @comment ListView_Item单击响应类
 *
 */
public class ListView_ItemClickListener implements
		AdapterView.OnItemClickListener {

	private ThrowAndCatchActivity m_activity;

	/**
	 * 构造函数
	 * @param activity
	 */
	public ListView_ItemClickListener(Activity activity) {
		m_activity = (ThrowAndCatchActivity)activity;
	}

	/**
	 * 继承单击响应函数
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ListView listView = (ListView) parent;
		
		FileAdapter fileAdapter = (FileAdapter)listView.getAdapter();
		FileObject fileObject = (FileObject)fileAdapter.getItem(position);
		String ext =  CommonUtility.getExtension(fileObject.fileName).toLowerCase();
//		Map<String, Object> item = (Map<String, Object>) lv
//				.getItemAtPosition(position);
//		String filename = (String) item.get("filename");
//		String ext = CommonUtility.getExtension(m_activity.getDataPath() + filename);

		// 如果是图片的话
		if (ext.compareTo(m_activity.getString(R.string.pic_JPEG)) == 0
				|| ext.compareTo(m_activity.getString(R.string.pic_GIF)) == 0
				|| ext.compareTo(m_activity.getString(R.string.pic_Bitmap)) == 0
				|| ext.compareTo(m_activity.getString(R.string.pic_PNG)) == 0) {

			// 切换Tab页
			TabHost tabHost = (TabHost) m_activity.findViewById(R.id.tabhost);
			tabHost.setCurrentTabByTag(m_activity.getString(R.string.tab_image));
			TextView tv = (TextView)m_activity.findViewById(R.id.tv_image);
			tv.setVisibility(View.INVISIBLE);

			ImageView imageView = (ImageView) m_activity
					.findViewById(R.id.imgv_image);
			//先回收，不然会崩溃，很重要！！！
			Drawable toRecycle= imageView.getDrawable();
			if(toRecycle != null){
				((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
			}			
			String path = m_activity.getDataPath() + fileObject.fileName;
			imageView.setImageDrawable(Drawable.createFromPath(path));
			imageView.setVisibility(View.VISIBLE);
		}
		// 如果是视频文件的话
		else if (ext.compareTo(m_activity.getString(R.string.vid_3GPP)) == 0
				|| ext.compareTo(m_activity.getString(R.string.vid_MPEG_4)) == 0) {

			// 切换Tab页
			TabHost tabHost = (TabHost) m_activity.findViewById(R.id.tabhost);
			tabHost.setCurrentTabByTag(m_activity.getString(R.string.tab_video));
			TextView tv = (TextView)m_activity.findViewById(R.id.tv_video);
			tv.setVisibility(View.INVISIBLE);
			
			// 播放视频
			VideoView video = (VideoView) m_activity
					.findViewById(R.id.vdv_video);
			String path = m_activity.getDataPath() + fileObject.fileName;
			video.setVideoPath(path);
			video.setMediaController(new MediaController(m_activity));
			video.setVisibility(View.VISIBLE);
			video.start();
		}
	}

}
