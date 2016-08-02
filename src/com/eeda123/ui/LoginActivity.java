package com.eeda123.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.eeda123.R;
import com.eeda123.bean.Constants;
import com.eeda123.task.Callback;
import com.eeda123.ui.base.BaseActivity;
import com.eeda123.ui.gateOut.SearchResultItem;
import com.eeda123.utils.CommonTools;
import com.eeda123.utils.EedaHttpClient;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private static final String Tag = "LoginActivity";
	private LoginActivity loginActivity = null;
	private ImageView loginLogo, login_more;
	private EditText loginaccount, loginpassword;
	private ToggleButton isShowPassword;
	private boolean isDisplayflag = false;// 是否显示密码
	private String getpassword;
	private Button loginBtn;
	private Intent mIntent;

//	public static String MOBILE_SERVERS_URL = "http://56.eeda123.com/login";
	String username;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginActivity = LoginActivity.this;
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		loginLogo = (ImageView) this.findViewById(R.id.logo);
		login_more = (ImageView) this.findViewById(R.id.login_more);
		loginaccount = (EditText) this.findViewById(R.id.loginaccount);
		loginpassword = (EditText) this.findViewById(R.id.loginpassword);

		isShowPassword = (ToggleButton) this.findViewById(R.id.isShowPassword);
		loginBtn = (Button) this.findViewById(R.id.login);

		getpassword = loginpassword.getText().toString();
	}

	@Override
	protected void initView() {

		isShowPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Log.i(Tag, "开关按钮状态=" + isChecked);
						if (isChecked) {
							// 隐藏
							loginpassword.setInputType(0x90);
							// loginpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
						} else {
							// 明文显示
							loginpassword.setInputType(0x81);
							// loginpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
						}
						Log.i("togglebutton", "" + isChecked);
						// loginpassword.postInvalidate();
					}
				});

		loginBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
    		case R.id.login:
    			doLogin();
    			//userlogin();
    			break;
    
    		default:
    			break;
		}

	}

	// 之前的方式太繁瑣了
	private void userlogin() {
		username = loginaccount.getText().toString().trim();
		password = loginpassword.getText().toString().trim();
		

		if (username.equals("")) {
			DisplayToast("用户名不能为空!");
		}
		if (password.equals("")) {
			DisplayToast("密码不能为空!");
		}
		
		if (username.equals("test") && password.equals("123")) {
			DisplayToast("登錄成功!");
			Intent data = new Intent();
			data.putExtra("name", username);
			// data.putExtra("values", 100);
			// 请求代码可以自己设置，这里设置成20
			setResult(20, data);

			LoginActivity.this.finish();
		}

		// new LoginTask().execute(username, password);

	}


	// 登录系统
	private void doLogin() {

		final String uaername = loginaccount.getText().toString().trim();
		final String password = loginpassword.getText().toString().trim();
		

		if (uaername.equals("")) {
			DisplayToast("用户名不能为空!");
		}
		if (password.equals("")) {
			DisplayToast("密码不能为空!");
		}

		new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BasicNameValuePair param1 = new BasicNameValuePair("username", uaername);
                    BasicNameValuePair param2 = new BasicNameValuePair("password", password);
                    BasicNameValuePair remember = new BasicNameValuePair("remember", "Y");
                    String response = EedaHttpClient.post("/login", param1, param2, remember);
                    
                    Message message = new Message(); 
                    message.what = 0;
                    // 将服务器返回的结果存放到Message中
                    message.obj = response;
                    handler.sendMessage(message);
                    
                } catch (ClientProtocolException e) {
                    Log.w(TAG, e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage());
                    e.printStackTrace();
                } 
            }
        }).start();

	}
	
	 private Handler handler = new Handler() {
	        public void handleMessage(Message msg) {
	            String responseJson = (String) msg.obj;
	            if(responseJson.indexOf("下次自动登录")>0){//登陆不成功
                    Toast.makeText(getApplicationContext(), "登录失败，用户名或密码不正确", Toast.LENGTH_LONG).show();
                }else{
                    
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("JSEESIONID", EedaHttpClient.getSessionId());
                    editor.commit();
                    
                    openActivity(HomeActivity.class);
                    overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);
                }
	        }
	    };

}
