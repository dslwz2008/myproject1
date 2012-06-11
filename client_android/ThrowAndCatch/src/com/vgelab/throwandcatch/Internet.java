package com.vgelab.throwandcatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Internet {

	/**
	 * ȡ����
	 * @param url ·��
	 * @return ����һ����
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream getStream(String url) throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(url));
        
        InputStream is = response.getEntity().getContent();
        return is;
	}
	
	/**
	 * ��������ȡ��һ���ַ���
	 * @param url ·��
	 * @return �����ַ���
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String getString(String url) throws ClientProtocolException, IOException
	{
		InputStream is = getStream(url);
		
		StringBuffer sb = new StringBuffer();   
        byte[] buffer = new byte[1024];   
        int n = is.read(buffer);  
        while (n!= -1){
            sb.append(new String(buffer, 0, n, "UTF-8"));
            n = is.read(buffer);
        }
        
        is.close();
        
        
        return  sb.toString();
	}

	/**
	 * ȡ��һ��ͼƬ
	 * @param url ·��
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Bitmap getImage(String url) throws ClientProtocolException, IOException
	{
		InputStream stream = getStream(url);
		Bitmap bmp = BitmapFactory.decodeStream(stream);
        return bmp;
	}
	
	/**
	 * �����Ƿ��������
	 * @param url ·��
	 * @return �Ƿ��ܹ�����
	 */
	public static boolean test(String url)
	{
		try {
			Internet.getString(url);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * ȡ��һ���ļ�
	 * @param url ·��
	 * @param saveFolder ���������ļ��ı����ļ���
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void getFile(String url, String saveFolder) throws ClientProtocolException, IOException
	{
		// ������
		InputStream is = getStream(url);
		
		String fileName = URLDecoder.decode(url.substring(url.lastIndexOf('/') + 1));
		File file = new File(saveFolder, fileName);
		
		// ���������ļ� 
		FileOutputStream fileStream = new FileOutputStream(file);
		
		// д���ļ�
		byte[] buffer = new byte[1024]; 
        int n = is.read(buffer);  
        while (n!= -1){
        	fileStream.write(buffer, 0, n);
            n = is.read(buffer);
        }
        
        fileStream.close();
        is.close();
	}
	
	/**
	 * �ύ�ļ�
	 * @param url �ύ�ļ���Url
	 * @param path ���ύ�ı����ļ�·��
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void postFile(String url, String path) throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		
        HttpPost post = new HttpPost(url);
        File file = new File(path);
        FileInputStream is = new FileInputStream(file);
        InputStreamEntity entity = new InputStreamEntity(
        		is, file.length());
        post.setEntity(entity);
        
        client.execute(post);
	}
}
