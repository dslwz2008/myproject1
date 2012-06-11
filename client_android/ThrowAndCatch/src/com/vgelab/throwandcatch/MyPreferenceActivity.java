package com.vgelab.throwandcatch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 自定义的设置页面
 */
public final class MyPreferenceActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}
	
	/**
	 * Activity返回时调用
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (RequestCode.SELECT_DIR_REQUEST):{
			if (resultCode == Activity.RESULT_OK) {
				String filePath = data.getStringExtra(FileDialog.RESULT_PATH) + "/";
				//Toast.makeText(getApplicationContext(), filePath, Toast.LENGTH_SHORT).show();
				ListView listview = getListView();
				for(int i = 0; i < listview.getChildCount(); i++){
					View view_out = listview.getChildAt(i);
					if(view_out.getId() == R.id.lyt_FileDialogPref){
						LinearLayout lyt = (LinearLayout)view_out;
						for(int j = 0; j < lyt.getChildCount(); j++){
							View view_in = lyt.getChildAt(j);
							if(view_in.getId() == R.id.btn_gotoselect){
								Button selectButton = (Button)view_in;
								selectButton.setText(filePath);
								break;
							}
						}
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString(getString(R.string.str_local_data_path), filePath);
						editor.commit();
						break;
					}
				}
			}
		}
			break;
		default:
			break;
		}
	}
}
