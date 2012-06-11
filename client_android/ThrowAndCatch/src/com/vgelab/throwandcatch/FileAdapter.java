package com.vgelab.throwandcatch;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * �ļ�������
 */
public class FileAdapter extends BaseAdapter {
	Context context;
	List<FileObject> data;
	LayoutInflater inflater;

	/**
	 * ������
	 * @param context ��ǰcontext
	 * @param data ����
	 */
	public FileAdapter(Context context, List<FileObject> data) {
		this.context = context;
		this.data = data;

		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * ����ĳһ���Ƿ�ѡ��
	 * @param position λ�ã���0��ʼ
	 * @param isChecked �Ƿ�ѡ��
	 */
	public void setItemChecked(int position, boolean isChecked) {
		data.get(position).isChecked = isChecked;
	}

	/**
	 * ȡ��ǰλ�õ���ͼ����д��BaseAdapter�ķ���
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView textView;
		CheckBox checkBox;
		FileObject file = data.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.simple_listview, null);
		} 
		
		textView = (TextView) convertView.findViewById(R.id.filename);
		textView.setText(file.fileName);

		checkBox = (CheckBox) convertView.findViewById(R.id.chb_select);
		checkBox.setChecked(file.isChecked);
		checkBox.setOnClickListener(new CheckBoxOnClickListener(file));
		
		return convertView;
	}

	/**
	 * �ڲ��࣬����CheckBox�ĵ���¼�
	 */
	public class CheckBoxOnClickListener implements View.OnClickListener {
		FileObject fileObject;

		public CheckBoxOnClickListener(FileObject fileObject) {
			this.fileObject = fileObject;
		}

		public void onClick(View v) {
			fileObject.isChecked = !fileObject.isChecked;
			((CheckBox) v).setChecked(fileObject.isChecked);
		}
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return data.get(position).id;
	}
}
