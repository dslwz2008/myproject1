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
	 * 取得流
	 * @param url 路径
	 * @return 返回一个流
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
	 * 从网络上取得一个字符串
	 * @param url 路径
	 * @return 返回字符串
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
	 * 取回一个图片
	 * @param url 路径
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
	 * 测试是否可以连接
	 * @param url 路径
	 * @return 是否能够连接
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
	 * 取回一个文件
	 * @param url 路径
	 * @param saveFolder 用来保存文件的本地文件夹
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void getFile(String url, String saveFolder) throws ClientProtocolException, IOException
	{
		// 读入流
		InputStream is = getStream(url);
		
		String fileName = URLDecoder.decode(url.substring(url.lastIndexOf('/') + 1));
		File file = new File(saveFolder, fileName);
		
		// 创建本地文件 
		FileOutputStream fileStream = new FileOutputStream(file);
		
		// 写入文件
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
	 * 提交文件
	 * @param url 提交文件的Url
	 * @param path 待提交的本地文件路径
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
