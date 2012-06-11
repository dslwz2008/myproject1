package com.vgelab.throwandcatch;

/**
 * �ļ�����
 */
public final class FileObject
{
	public long id;
	public String fileName;
	public boolean isChecked;
	
	public FileObject()
	{
	}
	
	/**
	 * ��ͨ�Ĺ���
	 * @param id ���б��е�id
	 * @param fileName �ļ���
	 */
	public FileObject(long id, String fileName)
	{
		this.id = id;
		this.fileName = fileName;
	}
	
	@Override
	public String toString()
	{
		return this.fileName;
	}
}
