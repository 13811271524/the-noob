package com.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.widget.Toast;

/**
 * 该类被设计成单例模式,包含常用方法和数据
 * 1.每个Activity在onCreate时，调用MyApplication.getInstance().addActivity(this);
 * 2.如果想退出，在你的APP的首屏，监听"回退"键，当点击，调用MyApplication.getInstance().exit();
 * @author Administrator
 * 
 */
public class MyApplication extends Application {
	private static LinkedList<Activity> activityList = new LinkedList<Activity>();//使用集合类统一管理Activity实例
	private static MyApplication instance;
	public static JSONObject favors;//用户关注的商品、店铺动态数
	public static JSONArray cate;
	public static JSONArray cate_zhuti;
	public static JSONArray cate_brand;
	public static int gridColumns=2;//格子列数
	public static String nickname;
	public static int screenWidth;
	public static int screenHeight;
	public static int waterFullPageSize;//瀑布流每页大小
	public static String site = "http://192.168.8.5/";
//	public static String site = "http://www.wotaowotao.com/";
	private MyApplication() { }
	
	public static MyApplication getInstance() {
		if (instance == null)
			instance = new MyApplication();
		return instance;
	}

	/** 添加Activity到容器，在每个Activity的onCreate调用 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/** 遍历Activity并finish，在TabsActivity的主界面当点击back键调用 */
	public static void exit() {
		//遍历所有Activity实例，挨个finish
		for (Activity activity : activityList) {
			if(activity!=null)
				activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());//获取当前进程PID，并杀掉
	}

	/** 发送GET请求并获取服务器端返回值 */
	public static String handleGet(String strUrl) {
		String result = null;
		HttpGet request = new HttpGet(strUrl);//实例化get请求
		DefaultHttpClient client = new DefaultHttpClient();//实例化客户端
		try {
			HttpResponse response = client.execute(request);//执行该请求,得到服务器端的响应内容
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),"GBK");//把响应结果转成String
			} else {
				result = response.getStatusLine().toString();
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	/** 发送GET请求，返回byte数组(针对图片等二进制数据) */
	public static byte[] handleGetForBinary(String strUrl) {
		byte[] result = null;
		HttpGet request = new HttpGet(strUrl);
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
			} else {
				result = new String("读取错误").getBytes();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	/** 携带一个params数据发送Post请求到指Url */
	public String handlePost(String strUrl, List<NameValuePair> params) {
		String result = null;
		HttpPost request = new HttpPost(strUrl);
		try {
			request.setEntity(new UrlEncodedFormEntity(params, "GBK"));	
			HttpResponse response = new DefaultHttpClient().execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			} else {
				result = response.getStatusLine().toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/** Drawable转成Bitmap */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/** 将JSON字符串转换为Map */
	public static Map<String, Object> getMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 将JSON字符串转换为ArrayList */
	public static List<Map<String, Object>> getList(String jsonString) {
		List<Map<String, Object>> list = null;
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			JSONObject jsonObject;
			list = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(getMap(jsonObject.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/** 合并两个JSONArray */
	public static JSONArray joinJSONArray(JSONArray mData, JSONArray array) {
		StringBuffer buffer = new StringBuffer();	
		try {
			int len = mData.length();
			if(len==0)
				return array;
			for (int i = 0; i < len; i++) {
				JSONObject obj1 = (JSONObject) mData.get(i);
				if(i==len-1)
					buffer.append(obj1.toString());
				else
					buffer.append(obj1.toString()).append(",");
			}			
			//JSONArray array = obj.getJSONArray("items");
			len = array.length();
			if(len>0) buffer.append(",");
			for (int i = 0; i < len; i++) {
				JSONObject obj1 = (JSONObject) array.get(i);
				if(i==len-1)
					buffer.append(obj1.toString());
				else
					buffer.append(obj1.toString()).append(",");
			}									
			buffer.insert(0, "[").append("]");
			System.out.println(buffer);
			return new JSONArray(buffer.toString());
		} catch (Exception e) {
		}
		return null;
	}

	//日期格式化
	public static String parseDate(String strDate) {		
		try {
			SimpleDateFormat sf = new SimpleDateFormat("mm月dd日");
			return sf.parse(strDate+" 00:00:00.0").toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	public static int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}
	/** 图片网址转化为Drawable */
	public static Drawable getDrawable(String strUrl) {  
	    Drawable drawable = null;  
	    InputStream is = null;  
	    try {  
	        URL url = new URL(strUrl);  
	        URLConnection conn = url.openConnection();  
	        is = conn.getInputStream();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	    drawable = Drawable.createFromStream(is, "src");  
	    return drawable;  
	}   
	/** 检测网络连接状态 */
	public static void checkNetworkState(Context context) {
		State wifiState = null;
		State mobileState = null;
	
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);	
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			Toast.makeText(context, "手机网络连接成功", Toast.LENGTH_SHORT).show();
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			Toast.makeText(context, "手机没有任何的网络", Toast.LENGTH_SHORT).show();
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			Toast.makeText(context, "无线网络连接成功", Toast.LENGTH_SHORT).show();
		}
		
		NetworkInfo info = cm.getActiveNetworkInfo();//获取活动的网络连接信息
		if(info!=null && info.isAvailable()) {//能连上某个路由器即返回true
			Toast.makeText(context, "网络可用", Toast.LENGTH_SHORT).show();
		} else if(info!=null && info.isAvailable() && info.isConnected()) {//能连上路由器并且能真正上网
			Toast.makeText(context, "网络可用并且已连接上", Toast.LENGTH_SHORT).show();
		} else {//没有连接上路由器
			Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
		}
		
	}
}