package com.vgelab.throwandcatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author shenshen, xukailun
 * @comment 主窗体类（负责逻辑比较多，所以比较乱）
 *
 */
public class ThrowAndCatchActivity extends Activity {
	/**
	 *  传感器管理器
	 */
	private SensorManager m_manager;
	
	/**
	 *  加速度计
	 */
	private Sensor m_accelerometer;
	
	/**
	 *  加速度计监听器
	 */
	private AccelerometerEventListener m_accelroEL;
	
	/**
	 *  用于播放音频
	 */
	private MediaPlayer m_mediaPlayer;
	
	/**
	 *  设置
	 */
	private SharedPreferences m_prefs;

	/**
	 * @return 获取服务器的路径
	 */
	public String getServerURL() {
		String serverUrl = m_prefs.getString(getString(R.string.str_server_url), "");
		if (serverUrl == "")
			return "";
		else
			return String.format(getString(R.string.url_server), serverUrl);
	}

	/**
	 * @return 获取服务器Test的路径
	 */
	public String getTestServerURL() {
		String serverUrl = m_prefs.getString(getString(R.string.str_server_url), "");
		if (serverUrl == "")
			return "";
		else
			return String.format(getString(R.string.url_test), serverUrl);
	}

	/**
	 * @return 获取服务器数据文件夹的路径
	 */
	public String getServerFolderURL() {
		String serverUrl = m_prefs.getString(getString(R.string.str_server_url), "");
		if (serverUrl == "")
			return "";
		else
			return String.format(getString(R.string.url_folder_content),
					serverUrl);
	}
	
	/**
	 * 文件夹是否存在
	 */
	private boolean m_dirExists = false;

	/**
	 * 取得本地目录完整路径
	 * @return
	 */
	public String getDataPath() {
		String dataPath = m_prefs.getString(getString(R.string.str_local_data_path), "");
		if (dataPath == "") {
			dataPath = Environment.getExternalStorageDirectory().getPath()
					+ "/" + getString(R.string.app_name) + "/data/";
		}
		return dataPath;
	}

	/**
	 * 创建窗体
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		checkSDCard();
		
		m_prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(m_prefs.getString(getString(R.string.str_local_data_path), "")=="")
		{
			SharedPreferences.Editor editor = m_prefs.edit();
			//赋予false
			editor.putString(getString(R.string.str_local_data_path), Environment.getExternalStorageDirectory().getPath() + "/"
					+ getString(R.string.app_name) + "/data/");
			editor.commit();
		}

		checkDataState();
		initDataDirectory();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// TabHost
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		tabhost.setup();

		// 4个Tab页
		LayoutInflater li = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View promptsView = li.inflate(R.layout.prompt_dialog, null);
		createTabItem(li, R.string.tab_files, R.id.lyt_file, getResources()
				.getDrawable(R.drawable.directory), true);
		createTabItem(li, R.string.tab_server, R.id.lyt_server, getResources()
				.getDrawable(R.drawable.directory), false);
		createTabItem(li, R.string.tab_image, R.id.lyt_picture, getResources()
				.getDrawable(R.drawable.image), false);
		createTabItem(li, R.string.tab_video, R.id.lyt_video, getResources()
				.getDrawable(R.drawable.video), false);

		tabhost.setOnTabChangedListener(new TabHost_TabChangedListener(this));
		tabhost.setCurrentTab(0);
		// 由于ListView不会自己刷新，所以要先调用一下
		listDeviceDataDirectory();

		m_manager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		m_accelerometer = m_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		m_accelroEL = new AccelerometerEventListener(this);

		Button btn_select_all = (Button) findViewById(R.id.select_all);
		btn_select_all.setOnClickListener(m_selectall_click);
		Button btn_select_some = (Button) findViewById(R.id.select_someone);
		btn_select_some.setOnClickListener(m_selectsome_click);
	}

	/**
	 * 检查SD卡是否正常
	 */
	private void checkSDCard() {
		if (!CommonUtility.hasSdcard()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.error_title);
			builder.setMessage(R.string.error_sdcard);
			builder.setPositiveButton("OK", finishAppListener);
			builder.create().show();
		}
	}

	/**
	 * 检查文件夹是否存在，如果存在只拷贝数据，并记录检查结果
	 */
	private void checkDataState() {
		String dataPath = getDataPath();

		File directory = new File(dataPath);
		if (!directory.isDirectory()) {
			m_dirExists = false;
		} else {
			// 检查assets下面的数据文件是否存在
			m_dirExists = true;
		}
	}

	/**
	 * 创建Tab页
	 * @param li LayoutInflater对象
	 * @param nameId 名称的Id
	 * @param contentId 内容的Id
	 * @param icon 图标
	 * @param selected 初始时是否选中
	 */
	private void createTabItem(LayoutInflater li, int nameId, int contentId,
			Drawable icon, boolean selected) {
		// 取名称
		String name = getString(nameId);

		// 设置Tab页内容
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		TabHost.TabSpec tab = tabhost.newTabSpec(name);
		tab.setContent(contentId);

		// 加载View
		View view = li.inflate(R.layout.tabheader, null);

		ImageView imageView = (ImageView) view
				.findViewById(R.id.tabheader_icon);
		imageView.setImageDrawable(icon);

		TextView textView = (TextView) view
				.findViewById(R.id.tabheader_caption);
		textView.setText(name);

		if (selected)
			view.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.bg_tabitem_selected));

		// 设置Tab页标签
		tab.setIndicator(view);

		tabhost.addTab(tab);
	}

	/**
	 * 初始化本地文件夹
	 */
	private void initDataDirectory() {

		// 不存在的话去创建文件夹
		if (!m_dirExists) {
			if (!CommonUtility.makeDirectory(getDataPath())) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.error_title);
				builder.setMessage(R.string.error_mkdir);
				builder.setPositiveButton("OK", finishAppListener);
				builder.create().show();
			}
		}

		// 拷贝数据
		copyAssets2SDCard();
	}

	/**
	 * 结束事件监听器
	 */
	private DialogInterface.OnClickListener finishAppListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			ThrowAndCatchActivity.this.finish();
		}
	};

	/**
	 * copy预置的资源到SD卡
	 */
	private void copyAssets2SDCard() {
		AssetManager am = getAssets();
		try {
			InputStream is = am.open("images/"
					+ getString(R.string.image_assets));
			String destFilePath = getDataPath()
					+ getString(R.string.image_assets);
			File destFile = new File(destFilePath);
			AndroidFileUtils.copyToFile(is, destFile);
			is.close();

			is = am.open("videos/" + getString(R.string.video_assets));
			destFilePath = getDataPath() + getString(R.string.video_assets);
			destFile = new File(destFilePath);
			AndroidFileUtils.copyToFile(is, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列出本地文件夹的内容
	 */
	void listDeviceDataDirectory() {

		File directory = new File(this.getDataPath());
		File[] files = directory.listFiles();
		
		List<FileObject> fileObjects = new ArrayList<FileObject>();
		for (int i = 0; i<files.length; i++) {
			fileObjects.add(new FileObject(i, files[i].getName()));
		}
		FileAdapter fileAdapter = new FileAdapter(this, fileObjects);
		ListView lst_files = (ListView) this.findViewById(R.id.ltv_files);
		lst_files.setAdapter(fileAdapter);
		lst_files.setOnItemClickListener(new ListView_ItemClickListener(this));
	}

	/**
	 * 列出服务端文件夹的内容
	 */
	void listServerDataDirectory() {
		// check server path
		if (!Internet.test(getServerURL())) {
			Toast.makeText(getApplicationContext(),
					getString(R.string.error_server_connection),
					Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			String jsonContent = Internet.getString(getServerURL()
					+ "FolderContent/");
			JSONObject jsonObj = new JSONObject(jsonContent);
			JSONArray arrValue = jsonObj.getJSONArray("content");

			ListView lst_files = (ListView) this.findViewById(R.id.ltv_server);

			List<FileObject> fileObjects = new ArrayList<FileObject>();
			for (int i = 0; i < arrValue.length(); i++) {
				fileObjects.add(new FileObject(i, arrValue.getString(i)));
			}
			FileAdapter fileAdapter = new FileAdapter(this, fileObjects);
			lst_files.setAdapter(fileAdapter);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 全选
	 * @param id 
	 */
	private void selectedAllItemsForListView(int id) {
		selectedItemsForListView(id, PreOrPost.All, -1);
	}

	/**
	 * 全不选
	 * @param id 
	 */
	private void unselectedAllItemsForListView(int id) {
		selectedItemsForListView(id, PreOrPost.None, -1);
	}

	/**
	 * 选前N项
	 * @param id ListView的Id
	 * @param count 选择项的数量
	 */
	private void selectedPreItemsForListView(int id, int count) {
		selectedItemsForListView(id, PreOrPost.Pre, count);
	}

	/**
	 * 选后N项
	 * @param id ListView的Id
	 * @param count 选择项的数量
	 */
	private void selectedPostItemsForListView(int id, int count) {
		selectedItemsForListView(id, PreOrPost.Post, count);
	}

	/**
	 * 选择项目模式
	 */
	enum PreOrPost {
		All, Pre, Post, None 
	}
	/**
	 * 选择项
	 * @param id ListView的Id
	 * @param preOrPost 从前选或是从后选
	 * @param count 数量
	 */
	private void selectedItemsForListView(int id, PreOrPost preOrPost, int count) {
		ListView listView = (ListView) this.findViewById(id);
		FileAdapter fileAdapter = (FileAdapter)listView.getAdapter();
		for (int i = 0; i < listView.getCount(); i++) {

			boolean check = false;
			switch(preOrPost)
			{
			case All:
				check = true;
				break;
			case Pre:
				check = i < count;
				break;
			case Post:
				check = i >= listView.getCount() - count;
				break;
			}
			fileAdapter.setItemChecked(i, check);
		}
		fileAdapter.notifyDataSetChanged();
	}

	/**
	 * 取得ListView中选中的项目
	 * @param id ListView的Id
	 * @return
	 */
	private List<String> getListViewSelectedItems(int id) {
		// 取得所有选中文件
		List<String> items = new ArrayList<String>();

		ListView listView = (ListView) this.findViewById(id);
		for (int i = 0; i < listView.getChildCount(); i++) {

			View view = listView.getChildAt(i);
			CheckBox cb = (CheckBox) view.findViewById(R.id.chb_select);

			if (cb.isChecked()) {
				TextView text = (TextView) view.findViewById(R.id.filename);
				items.add((String) text.getText());
			}
		}
		return items;
	}

	/**
	 * 创建Menu事件
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Menu打开事件
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		TabHost tab = (TabHost) findViewById(R.id.tabhost);
		switch (tab.getCurrentTab()) {
		case 0:
			menu.findItem(R.id.send_button).setEnabled(true);
			menu.findItem(R.id.receive_button).setEnabled(false);
			break;
		case 1:
			menu.findItem(R.id.send_button).setEnabled(false);
			menu.findItem(R.id.receive_button).setEnabled(true);
			break;
		case 2:
		case 3:
			menu.findItem(R.id.send_button).setEnabled(false);
			menu.findItem(R.id.receive_button).setEnabled(false);
		}

		return true;
	}

	/**
	 * 选择Item事件
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.send_button:
			SendFiles();
			break;

		case R.id.receive_button:
			ReceiveFiles();
			break;

		case R.id.setting_button:
			Settings();
			break;
		}
		return false;
	}

	/**
	 * 发送文件
	 */
	void SendFiles() {
		List<String> files;
		if (getServerURL() == "") {
			Toast.makeText(this, R.string.error_server_connection,
					Toast.LENGTH_SHORT).show();
			return;
		}

		// 上传所有选中文件
		files = getListViewSelectedItems(R.id.ltv_files);
		if (files.isEmpty()) {
			Toast.makeText(this, "未选中任何数据！", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "开始上传...", Toast.LENGTH_SHORT).show();
		for (String file : files) {
			try {
				Internet.postFile(getServerURL() + "/PostResourceContent/"
						+ URLEncoder.encode(file), getDataPath() + file);
				// Toast.makeText(this, "上传文件\"" + file + "\"完成!",
				// Toast.LENGTH_SHORT).show();
				playLocalAudio(R.raw.file_complete);
			} catch (Exception e) {
				Toast.makeText(this, "上传失败：" + file, Toast.LENGTH_SHORT).show();
			}
		}
		Toast.makeText(this, "完成上传", Toast.LENGTH_SHORT).show();
		playLocalAudio(R.raw.mission_complete);
	}

	/**
	 * 播放本地声音
	 * @param resId 声音的Id
	 */
	private void playLocalAudio(int resId) {
		m_mediaPlayer = MediaPlayer.create(this, resId);
		m_mediaPlayer.start();
	}

	/**
	 * 接收所有文件
	 */
	void ReceiveFiles() {
		List<String> files;
		// 下载所有选中文件
		files = getListViewSelectedItems(R.id.ltv_server);
		if (files.isEmpty()) {
			Toast.makeText(this, "未选中任何数据！", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "开始下载...", Toast.LENGTH_SHORT).show();
		for (String file : files) {
			try {
				Internet.getFile(getServerURL() + "ResourceContent/"
						+ URLEncoder.encode(file), getDataPath());
				// Toast.makeText(this, "下载文件\"" + file + "\"完成!",
				// Toast.LENGTH_SHORT).show();
				playLocalAudio(R.raw.file_complete);
			} catch (Exception e) {
				Toast.makeText(this, "下载失败：" + file, Toast.LENGTH_SHORT).show();
			}
		}
		Toast.makeText(this, "完成下载", Toast.LENGTH_SHORT).show();
		playLocalAudio(R.raw.mission_complete);
	}

	/**
	 * 设定
	 */
	private void Settings() {
		try {
			Intent intent = new Intent().setClass(this,
					MyPreferenceActivity.class);
			this.startActivityForResult(intent, RequestCode.SETTINGS_REQUEST);

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void onResume() {
		m_manager.registerListener(m_accelroEL, m_accelerometer,
				SensorManager.SENSOR_DELAY_UI);
		super.onResume();
	}

	protected void onPause() {
		m_manager.unregisterListener(m_accelroEL, m_accelerometer);
		super.onPause();
	}

	/**
	 * 全部选择ListView Item
	 */
	private OnClickListener m_selectall_click = new OnClickListener() {

		public void onClick(View v) {
			TabHost tab = (TabHost) findViewById(R.id.tabhost);
			if (tab.getCurrentTab() == 0) {
				Button button = (Button) v;
				// 全选的情况
				if (button.getText() == getString(R.string.select_all)) {
					selectedAllItemsForListView(R.id.ltv_files);
					button.setText(getString(R.string.select_none));
				} else {
					unselectedAllItemsForListView(R.id.ltv_files);
					button.setText(getString(R.string.select_all));
				}
			} else if (tab.getCurrentTab() == 1)
				selectedAllItemsForListView(R.id.ltv_server);
		}
	};
	
	/**
	 * 选择部分ListView Item
	 */
	private OnClickListener m_selectsome_click = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), SelectModeActivity.class);
			// 可以传数据
			ListView list = getCurrentList();
			intent.putExtra(getString(R.string.lbl_extra_count),
					list.getCount());
			startActivityForResult(intent, RequestCode.SELECT_MODE_REQUEST);
		}
	};

	/**
	 * 接收其他窗体返回事件，包括数据
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (RequestCode.SELECT_MODE_REQUEST): {
			if (resultCode == Activity.RESULT_OK) {
				// 提取数据
				ListView list = getCurrentList();
				int start = data.getIntExtra(
						getString(R.string.lbl_extra_start), -1);
				int end = data.getIntExtra(getString(R.string.lbl_extra_end),
						-1);
				if (start == -1) {// 从后面选
					selectedPostItemsForListView(list.getId(), end);
				} else if (end == -1) {// 从前面选
					selectedPreItemsForListView(list.getId(), start);
				}
			}
			break;
		}
		case (RequestCode.SETTINGS_REQUEST):{
			listDeviceDataDirectory();//是要刷新一下文件夹
		}
			break;
		default:
			break;
		}
	}

	/**
	 * 取当前列表
	 * @return
	 */
	private ListView getCurrentList() {
		TabHost tab = (TabHost) findViewById(R.id.tabhost);
		ListView list = null;
		// 与tab 2、3无关
		if (tab.getCurrentTab() == 0) {
			list = (ListView) findViewById(R.id.ltv_files);
		} else if (tab.getCurrentTab() == 1) {
			list = (ListView) findViewById(R.id.ltv_server);
		}
		return list;
	}
}
