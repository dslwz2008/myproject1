package com.vgelab.throwandcatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * 
 * @author shenshen
 * @email dslwz2008@gmail.com
 * @version 1.0
 * @comment 选择模式窗体
 *
 */
public class SelectModeActivity extends Activity {
	RadioGroup m_group;
	RadioButton m_btnStart;
	RadioButton m_btnEnd;
	EditText m_edtStart;
	EditText m_edtEnd;
	Button m_btnOK;
	Button m_btnCancel;
	int m_count;
	
	/**
	 * 创建窗体
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmode_activity);

		m_group = (RadioGroup) findViewById(R.id.rd_group);
		m_edtStart = (EditText) findViewById(R.id.edt_start);
		m_edtStart.setEnabled(false);
		m_edtEnd = (EditText) findViewById(R.id.edt_end);
		m_edtEnd.setEnabled(false);
		m_btnStart = (RadioButton) findViewById(R.id.rdb_start);
		m_btnEnd = (RadioButton) findViewById(R.id.rdb_end);
		m_btnOK = (Button) findViewById(R.id.btn_OK);
		m_btnCancel = (Button) findViewById(R.id.btn_Cancel);
		m_count = getIntent().getIntExtra(getString(R.string.lbl_extra_count), -1);
		
		m_btnOK.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent resultIntent = new Intent();
				int checkedId = m_group.getCheckedRadioButtonId();
				if(checkedId == m_btnStart.getId()){
					int number = Integer.parseInt(m_edtStart.getText().toString());
					//检查数字在有效范围内
					if(!checkNumber(number)){
						return;
					}
					resultIntent.putExtra(getString(R.string.lbl_extra_start), number);
				} else  if(checkedId == m_btnEnd.getId()){
					int number = Integer.parseInt(m_edtEnd.getText().toString());
					//检查数字在有效范围内
					if(!checkNumber(number)){
						return;
					}
					resultIntent.putExtra(getString(R.string.lbl_extra_end), number);
				}else{
					//impossible to reach
				}
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

		m_btnCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		m_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rdb_start:
					m_edtStart.setEnabled(true);
					m_edtStart.requestFocus();
					m_edtEnd.setEnabled(false);
					break;
				case R.id.rdb_end:
					m_edtEnd.setEnabled(true);
					m_edtEnd.requestFocus();
					m_edtStart.setEnabled(false);
					break;
				default:
					break;
				}
			}
		});

	}
	
	/**
	 * 检查传入的数字是否比当前的数字要大
	 * @param number
	 * @return 
	 */
	private boolean checkNumber(int number){
		if(number > m_count){
			Toast.makeText(getApplicationContext(), getString(R.string.error_number_too_big)
					, Toast.LENGTH_SHORT).show();
			return false;
		}else {
			return true;
		}
	}
}