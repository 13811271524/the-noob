package com.test.util;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.Handler;
import android.os.Message;

/**
 * 下载指定URL返回的JSON数据
 * @author david
 */
public class MyThread extends Thread {
	private String url;
	private int what;
	private Handler handler;
	
	public MyThread(String url, int what, Handler handler){
		this.url = url;
		this.what = what;
		this.handler = handler;
	}
	@Override
	public void run() {
		try {
			String result = MyApplication.handleGet(MyApplication.site + url);
			if (result != null && !result.equals("")) {
				JSONObject data = new JSONObject(result);
				Message msg = new Message();
				msg.what = what;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		} catch (JSONException e) {
			System.out.println("线程获取数据异常：" + url);
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(-1, null));
		}
	}
}