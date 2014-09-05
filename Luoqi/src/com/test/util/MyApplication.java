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
 * ���౻��Ƴɵ���ģʽ,�������÷���������
 * 1.ÿ��Activity��onCreateʱ������MyApplication.getInstance().addActivity(this);
 * 2.������˳��������APP������������"����"���������������MyApplication.getInstance().exit();
 * @author Administrator
 * 
 */
public class MyApplication extends Application {
	private static LinkedList<Activity> activityList = new LinkedList<Activity>();//ʹ�ü�����ͳһ����Activityʵ��
	private static MyApplication instance;
	public static JSONObject favors;//�û���ע����Ʒ�����̶�̬��
	public static JSONArray cate;
	public static JSONArray cate_zhuti;
	public static JSONArray cate_brand;
	public static int gridColumns=2;//��������
	public static String nickname;
	public static int screenWidth;
	public static int screenHeight;
	public static int waterFullPageSize;//�ٲ���ÿҳ��С
	public static String site = "http://192.168.8.5/";
//	public static String site = "http://www.wotaowotao.com/";
	private MyApplication() { }
	
	public static MyApplication getInstance() {
		if (instance == null)
			instance = new MyApplication();
		return instance;
	}

	/** ���Activity����������ÿ��Activity��onCreate���� */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/** ����Activity��finish����TabsActivity�������浱���back������ */
	public static void exit() {
		//��������Activityʵ��������finish
		for (Activity activity : activityList) {
			if(activity!=null)
				activity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());//��ȡ��ǰ����PID����ɱ��
	}

	/** ����GET���󲢻�ȡ�������˷���ֵ */
	public static String handleGet(String strUrl) {
		String result = null;
		HttpGet request = new HttpGet(strUrl);//ʵ����get����
		DefaultHttpClient client = new DefaultHttpClient();//ʵ�����ͻ���
		try {
			HttpResponse response = client.execute(request);//ִ�и�����,�õ��������˵���Ӧ����
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),"GBK");//����Ӧ���ת��String
			} else {
				result = response.getStatusLine().toString();
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return result;
	}
	
	/** ����GET���󣬷���byte����(���ͼƬ�ȶ���������) */
	public static byte[] handleGetForBinary(String strUrl) {
		byte[] result = null;
		HttpGet request = new HttpGet(strUrl);
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
			} else {
				result = new String("��ȡ����").getBytes();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}

	/** Я��һ��params���ݷ���Post����ָUrl */
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
	
	/** Drawableת��Bitmap */
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

	/** ��JSON�ַ���ת��ΪMap */
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

	/** ��JSON�ַ���ת��ΪArrayList */
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
	
	/** �ϲ�����JSONArray */
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

	//���ڸ�ʽ��
	public static String parseDate(String strDate) {		
		try {
			SimpleDateFormat sf = new SimpleDateFormat("mm��dd��");
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
	/** ͼƬ��ַת��ΪDrawable */
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
	/** �����������״̬ */
	public static void checkNetworkState(Context context) {
		State wifiState = null;
		State mobileState = null;
	
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);	
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			Toast.makeText(context, "�ֻ��������ӳɹ�", Toast.LENGTH_SHORT).show();
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			Toast.makeText(context, "�ֻ�û���κε�����", Toast.LENGTH_SHORT).show();
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			Toast.makeText(context, "�����������ӳɹ�", Toast.LENGTH_SHORT).show();
		}
		
		NetworkInfo info = cm.getActiveNetworkInfo();//��ȡ�������������Ϣ
		if(info!=null && info.isAvailable()) {//������ĳ��·����������true
			Toast.makeText(context, "�������", Toast.LENGTH_SHORT).show();
		} else if(info!=null && info.isAvailable() && info.isConnected()) {//������·������������������
			Toast.makeText(context, "������ò�����������", Toast.LENGTH_SHORT).show();
		} else {//û��������·����
			Toast.makeText(context, "���粻����", Toast.LENGTH_SHORT).show();
		}
		
	}
}