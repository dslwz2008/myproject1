package com.vgelab.throwandcatch;

/**
 * 文件对象
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
	 * 普通的构造
	 * @param id 在列表中的id
	 * @param fileName 文件名
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
