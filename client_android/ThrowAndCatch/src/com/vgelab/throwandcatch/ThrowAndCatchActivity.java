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
 * @comment �������ࣨ�����߼��Ƚ϶࣬���ԱȽ��ң�
 *
 */
public class ThrowAndCatchActivity extends Activity {
	/**
	 *  ������������
	 */
	private SensorManager m_manager;
	
	/**
	 *  ���ٶȼ�
	 */
	private Sensor m_accelerometer;
	
	/**
	 *  ���ٶȼƼ�����
	 */
	private AccelerometerEventListener m_accelroEL;
	
	/**
	 *  ���ڲ�����Ƶ
	 */
	private MediaPlayer m_mediaPlayer;
	
	/**
	 *  ����
	 */
	private SharedPreferences m_prefs;

	/**
	 * @return ��ȡ��������·��
	 */
	public String getServerURL() {
		String serverUrl = m_prefs.getString(getString(R.string.str_server_url), "");
		if (serverUrl == "")
			return "";
		else
			return String.format(getString(R.string.url_server), serverUrl);
	}

	/**
	 * @return ��ȡ������Test��·��
	 */
	public String getTestServerURL() {
		String serverUrl = m_prefs.getString(getString(R.string.str_server_url), "");
		if (serverUrl == "")
			return "";
		else
			return String.format(getString(R.string.url_test), serverUrl);
	}

	/**
	 * @return ��ȡ�����������ļ��е�·��
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
	 * �ļ����Ƿ����
	 */
	private boolean m_dirExists = false;

	/**
	 * ȡ�ñ���Ŀ¼����·��
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
	 * ��������
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		checkSDCard();
		
		m_prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if(m_prefs.getString(getString(R.string.str_local_data_path), "")=="")
		{
			SharedPreferences.Editor editor = m_prefs.edit();
			//����false
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

		// 4��Tabҳ
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
		// ����ListView�����Լ�ˢ�£�����Ҫ�ȵ���һ��
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
	 * ���SD���Ƿ�����
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
	 * ����ļ����Ƿ���ڣ��������ֻ�������ݣ�����¼�����
	 */
	private void checkDataState() {
		String dataPath = getDataPath();

		File directory = new File(dataPath);
		if (!directory.isDirectory()) {
			m_dirExists = false;
		} else {
			// ���assets����������ļ��Ƿ����
			m_dirExists = true;
		}
	}

	/**
	 * ����Tabҳ
	 * @param li LayoutInflater����
	 * @param nameId ���Ƶ�Id
	 * @param contentId ���ݵ�Id
	 * @param icon ͼ��
	 * @param selected ��ʼʱ�Ƿ�ѡ��
	 */
	private void createTabItem(LayoutInflater li, int nameId, int contentId,
			Drawable icon, boolean selected) {
		// ȡ����
		String name = getString(nameId);

		// ����Tabҳ����
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		TabHost.TabSpec tab = tabhost.newTabSpec(name);
		tab.setContent(contentId);

		// ����View
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

		// ����Tabҳ��ǩ
		tab.setIndicator(view);

		tabhost.addTab(tab);
	}

	/**
	 * ��ʼ�������ļ���
	 */
	private void initDataDirectory() {

		// �����ڵĻ�ȥ�����ļ���
		if (!m_dirExists) {
			if (!CommonUtility.makeDirectory(getDataPath())) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.error_title);
				builder.setMessage(R.string.error_mkdir);
				builder.setPositiveButton("OK", finishAppListener);
				builder.create().show();
			}
		}

		// ��������
		copyAssets2SDCard();
	}

	/**
	 * �����¼�������
	 */
	private DialogInterface.OnClickListener finishAppListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			ThrowAndCatchActivity.this.finish();
		}
	};

	/**
	 * copyԤ�õ���Դ��SD��
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
	 * �г������ļ��е�����
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
	 * �г�������ļ��е�����
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
	 * ȫѡ
	 * @param id 
	 */
	private void selectedAllItemsForListView(int id) {
		selectedItemsForListView(id, PreOrPost.All, -1);
	}

	/**
	 * ȫ��ѡ
	 * @param id 
	 */
	private void unselectedAllItemsForListView(int id) {
		selectedItemsForListView(id, PreOrPost.None, -1);
	}

	/**
	 * ѡǰN��
	 * @param id ListView��Id
	 * @param count ѡ���������
	 */
	private void selectedPreItemsForListView(int id, int count) {
		selectedItemsForListView(id, PreOrPost.Pre, count);
	}

	/**
	 * ѡ��N��
	 * @param id ListView��Id
	 * @param count ѡ���������
	 */
	private void selectedPostItemsForListView(int id, int count) {
		selectedItemsForListView(id, PreOrPost.Post, count);
	}

	/**
	 * ѡ����Ŀģʽ
	 */
	enum PreOrPost {
		All, Pre, Post, None 
	}
	/**
	 * ѡ����
	 * @param id ListView��Id
	 * @param preOrPost ��ǰѡ���ǴӺ�ѡ
	 * @param count ����
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
	 * ȡ��ListView��ѡ�е���Ŀ
	 * @param id ListView��Id
	 * @return
	 */
	private List<String> getListViewSelectedItems(int id) {
		// ȡ������ѡ���ļ�
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
	 * ����Menu�¼�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Menu���¼�
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
	 * ѡ��Item�¼�
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
	 * �����ļ�
	 */
	void SendFiles() {
		List<String> files;
		if (getServerURL() == "") {
			Toast.makeText(this, R.string.error_server_connection,
					Toast.LENGTH_SHORT).show();
			return;
		}

		// �ϴ�����ѡ���ļ�
		files = getListViewSelectedItems(R.id.ltv_files);
		if (files.isEmpty()) {
			Toast.makeText(this, "δѡ���κ����ݣ�", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "��ʼ�ϴ�...", Toast.LENGTH_SHORT).show();
		for (String file : files) {
			try {
				Internet.postFile(getServerURL() + "/PostResourceContent/"
						+ URLEncoder.encode(file), getDataPath() + file);
				// Toast.makeText(this, "�ϴ��ļ�\"" + file + "\"���!",
				// Toast.LENGTH_SHORT).show();
				playLocalAudio(R.raw.file_complete);
			} catch (Exception e) {
				Toast.makeText(this, "�ϴ�ʧ�ܣ�" + file, Toast.LENGTH_SHORT).show();
			}
		}
		Toast.makeText(this, "����ϴ�", Toast.LENGTH_SHORT).show();
		playLocalAudio(R.raw.mission_complete);
	}

	/**
	 * ���ű�������
	 * @param resId ������Id
	 */
	private void playLocalAudio(int resId) {
		m_mediaPlayer = MediaPlayer.create(this, resId);
		m_mediaPlayer.start();
	}

	/**
	 * ���������ļ�
	 */
	void ReceiveFiles() {
		List<String> files;
		// ��������ѡ���ļ�
		files = getListViewSelectedItems(R.id.ltv_server);
		if (files.isEmpty()) {
			Toast.makeText(this, "δѡ���κ����ݣ�", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(this, "��ʼ����...", Toast.LENGTH_SHORT).show();
		for (String file : files) {
			try {
				Internet.getFile(getServerURL() + "ResourceContent/"
						+ URLEncoder.encode(file), getDataPath());
				// Toast.makeText(this, "�����ļ�\"" + file + "\"���!",
				// Toast.LENGTH_SHORT).show();
				playLocalAudio(R.raw.file_complete);
			} catch (Exception e) {
				Toast.makeText(this, "����ʧ�ܣ�" + file, Toast.LENGTH_SHORT).show();
			}
		}
		Toast.makeText(this, "�������", Toast.LENGTH_SHORT).show();
		playLocalAudio(R.raw.mission_complete);
	}

	/**
	 * �趨
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
	 * ȫ��ѡ��ListView Item
	 */
	private OnClickListener m_selectall_click = new OnClickListener() {

		public void onClick(View v) {
			TabHost tab = (TabHost) findViewById(R.id.tabhost);
			if (tab.getCurrentTab() == 0) {
				Button button = (Button) v;
				// ȫѡ�����
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
	 * ѡ�񲿷�ListView Item
	 */
	private OnClickListener m_selectsome_click = new OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), SelectModeActivity.class);
			// ���Դ�����
			ListView list = getCurrentList();
			intent.putExtra(getString(R.string.lbl_extra_count),
					list.getCount());
			startActivityForResult(intent, RequestCode.SELECT_MODE_REQUEST);
		}
	};

	/**
	 * �����������巵���¼�����������
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (RequestCode.SELECT_MODE_REQUEST): {
			if (resultCode == Activity.RESULT_OK) {
				// ��ȡ����
				ListView list = getCurrentList();
				int start = data.getIntExtra(
						getString(R.string.lbl_extra_start), -1);
				int end = data.getIntExtra(getString(R.string.lbl_extra_end),
						-1);
				if (start == -1) {// �Ӻ���ѡ
					selectedPostItemsForListView(list.getId(), end);
				} else if (end == -1) {// ��ǰ��ѡ
					selectedPreItemsForListView(list.getId(), start);
				}
			}
			break;
		}
		case (RequestCode.SETTINGS_REQUEST):{
			listDeviceDataDirectory();//��Ҫˢ��һ���ļ���
		}
			break;
		default:
			break;
		}
	}

	/**
	 * ȡ��ǰ�б�
	 * @return
	 */
	private ListView getCurrentList() {
		TabHost tab = (TabHost) findViewById(R.id.tabhost);
		ListView list = null;
		// ��tab 2��3�޹�
		if (tab.getCurrentTab() == 0) {
			list = (ListView) findViewById(R.id.ltv_files);
		} else if (tab.getCurrentTab() == 1) {
			list = (ListView) findViewById(R.id.ltv_server);
		}
		return list;
	}
}
