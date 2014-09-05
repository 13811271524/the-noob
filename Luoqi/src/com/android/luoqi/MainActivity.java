package com.android.luoqi;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import java.io.IOException;  
import java.net.MalformedURLException;  
import java.net.URL;  
  

import org.json.JSONException;  
import org.json.JSONObject;  
  
  

//import com.tencent.plus.TouchView;  
import com.tencent.tauth.IUiListener;  
import com.tencent.tauth.Tencent;  
import com.tencent.tauth.UiError;  
import com.test.util.MyApplication;  
import com.test.util.MyThread;  
import com.test.util.MyUrlThread;  
  

import android.annotation.SuppressLint;  
import android.app.Activity;  
import android.content.Intent;  
import android.graphics.drawable.Drawable;  
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Handler.Callback;  
import android.os.Message;  
import android.os.StrictMode;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.inputmethod.BaseInputConnection;  
import android.widget.Button;  
import android.widget.CheckedTextView;  
import android.widget.ImageView;  
import android.widget.TextView;  
import android.widget.Toast;

/*public class MainActivity extends ActionBarActivity{
private Button tencentload;
private Button gmailload;
private Button details;
private Intent t_load;
private Intent g_load;
private Intent about;
*/

public class MainActivity extends Activity implements OnClickListener,Callback{ 
    private Button login_qq;  
    private TextView nickname;  
    private ImageView image;  
    private Tencent mTencent;  
    private String imageurl;  
    String url_qqlogin;  
    private String openid;  
    private String access_token;  
    private String SCOPE = "get_simple_userinfo,add_topic";  
    private static final String APP_ID = "101095315";  
    private String url = "https://graph.qq.com/user/get_user_info";  
    private Handler handler;  
    private StringBuilder sBuilder = new StringBuilder();  
    private Handler sHandler = new Handler(){  
        public void handleMessage(Message msg) {  
            if (msg.what == 2) {  
                String result = msg.obj.toString();  
                JSONObject data;  
                try {  
                    data = new JSONObject(result);  
                    String ret = data.getString("ret");  
                    String messsage = data.getString("msg");  
                    if (Integer.valueOf(ret) == 0) {  
                        String name = data.getString("nickname");  
                        sBuilder.append("nickname为："+name+"\n");  
                        String gender = data.getString("gender");  
                        sBuilder.append("gender为："+gender+"\n");  
                        imageurl = data.getString("figureurl_1");  
                        sBuilder.append("imageurl为："+imageurl+"\n");  
//                      image.setImageDrawable(Drawable.createFromStream(new URL(imageurl).openConnection()  
//                      .getInputStream(), "src"));  
                        url_qqlogin = "jiekou.php?code=qq_user&nickname="+name+"&email="+name+  
                                "@qq.com&figureurl="+imageurl+"&sex="+gender+  
                                "&openid="+openid+"&accesstoken="+access_token;  
                        System.out.println("url_qqlogin----"+url_qqlogin);  
                        new MyThread(url_qqlogin.toString(), 1,qqHandler).start();  
                    }  
  
                } catch (Exception e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
  
                System.out.println("sBuilder---"+sBuilder);  
                nickname.setText(sBuilder);  
                  
            }  
        };  
    };  
    private Handler qqHandler = new Handler(){  
        public void handleMessage(Message msg) {  
            if (msg.what==1) {  
                String result = msg.obj.toString();  
                JSONObject data;  
                try {  
                    data = new JSONObject(result);  
                    String nickname = data.getString("nickname");  
                    String message = data.getString("msg");  
                    sBuilder.append("QQ的nickname为："+nickname+"\n");  
                    sBuilder.append("QQ的message为："+message+"\n");  
                    System.out.println("333==="+sBuilder);  
                    Toast.makeText(MainActivity.this, message, 1000).show();  
                } catch (Exception e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
                nickname.setText(sBuilder);  
            }  
        };  
    }; 
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main);  
        handler = new Handler(this);  
        // Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。  
        // 其中APP_ID是分配给第三方应用的appid，类型为String。  
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());  
        login_qq = (Button) findViewById(R.id.T_load);  
        login_qq.setOnClickListener(this);  
//        nickname = (TextView) findViewById(R.id.nickname);  
        image = (ImageView) findViewById(R.id.image);  
          
    }  
    @Override  
    public void onClick(View v) {  
        // TODO Auto-generated method stub  
        switch (v.getId()) {  
            case R.id.T_load:  
                onClickLogin();  
                break;  
        }  
    }  
    //应用调用Andriod_SDK接口时，使能成功接收到回调  
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
         mTencent.onActivityResult(requestCode, resultCode, data);  
    }  
    private void onClickLogin() {  
        if (!mTencent.isSessionValid()) {  
            IUiListener listener = new IUiListener() {  
                /** 授权失败的回调*/  
                @Override  
                public void onError(UiError arg0) {  
                    // TODO Auto-generated method stub  
                    Toast.makeText(MainActivity.this, "授权失败", 1000).show();  
                    Message msg = new Message();  
                    msg.arg1 = 2;  
                    handler.sendMessage(msg);     
                }  
                /** 授权成功的回调*/  
                public void onComplete(JSONObject arg0) {  
                    // TODO Auto-generated method stub  
                    Toast.makeText(MainActivity.this, "授权成功", 1000).show();  
                    Message msg = new Message();  
                    msg.what = 2;  
                    msg.arg1 = 1;  
                    msg.obj = arg0;  
                    handler.sendMessage(msg);  
                }  
                /** 取消授权的回调*/  
                @Override  
                public void onCancel() {  
                    // TODO Auto-generated method stub  
                    Toast.makeText(MainActivity.this, "取消授权", 1000).show();  
                    Message msg = new Message();  
                    msg.arg1 = 3;  
                    handler.sendMessage(msg);     
                }
				@Override
				public void onComplete(Object arg0) {
					// TODO Auto-generated method stub
					
				}  
            };  
            mTencent.login(this, SCOPE, listener);  
        } else {  
            mTencent.logout(this);  
        }  
    }  
      
    @Override  
    public boolean handleMessage(Message msg) {  
        // TODO Auto-generated method stub  
        switch (msg.arg1) {  
            case 1: { // 成功  
                JSONObject object = (JSONObject) msg.obj;  
                try {  
                    openid = object.getString("openid").toString();  
                    sBuilder.append("openid为："+openid+"\n");  
                    access_token = object.getString("access_token").toString();  
                    url = url + "?access_token="+access_token+"&oauth_consumer_key="+APP_ID+  
                            "&openid="+openid+"&format=json";  
                    new MyUrlThread(url.toString(), 2,sHandler).start();  
                } catch (JSONException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
            break;  
            case 2: { // 失败  
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();  
                return false;  
            }  
            case 3: { // 取消  
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();  
                return false;  
            }  
        }  
        return false;  
    }  
 


/**
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
    tencentload = (Button) findViewById(R.id.T_load);
    tencentload.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
        t_load = new Intent();
        t_load.setClass(MainActivity.this,Tencent_loadwindow.class);
        startActivity(t_load);
        MainActivity.this.finish();
        }
    });          useless cods
    tencentload = (Button) findViewById(R.id.G_load);
    tencentload.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
        g_load = new Intent();
        g_load.setClass(MainActivity.this,Gmail_loadwindow.class);
        startActivity(g_load);
        MainActivity.this.finish();
        }
    });
    details = (Button) findViewById(R.id.detail);
    details.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
        about = new Intent();
        about.setClass(MainActivity.this,About.class);
        startActivity(about);
        MainActivity.this.finish();
        }
    });
*/
/*		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
/**	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
*/
}
