package com.eeda123.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.eeda123.ui.LoginActivity;
import com.eeda123.ui.SearchActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class EedaHttpClient {
    
    private static final String TAG = "EedaHttpClient";
    private static final String CHARSET = HTTP.UTF_8;  
    private static HttpClient customerHttpClient;  
    private static final String host="http://yd2demo.eeda123.com";
    //private static final String host="http://192.168.0.8:8080";
    
    /** 记住sessionId, rememberMe, shiro需要这两个cookie值来免登录 */  
    private static String sessionId = null;
    private static String rememberMe = null;  
    
    private EedaHttpClient() {  
    }
    
    public static void updateSessionId()  
    {  
        // 获取sessionId
        if(null==sessionId){
            List<Cookie> cookies = ((AbstractHttpClient)customerHttpClient).getCookieStore().getCookies();  
            if (cookies != null && cookies.size() > 0){  
                //这里是读取指定Cookie 的值  
                for (int i = 0; i < cookies.size(); i++) {   
                    if ("JSESSIONID".equals(cookies.get(i).getName())) {   
                        sessionId = cookies.get(i).getValue();  
                       continue;
                    }
                    if ("rememberMe".equals(cookies.get(i).getName())) {   
                        rememberMe = cookies.get(i).getValue();  
                       continue;
                    }
                }
            } 
        }
    }
    
    public static String getSessionId(){
        return sessionId;
    }
    
    public static void setSessionId(String _sessionId){
        sessionId=_sessionId;
    }
    
    public static synchronized HttpClient getHttpClient() {  
        if (null == customerHttpClient) {  
            HttpParams params = new BasicHttpParams();  
            // 设置一些基本参数  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
            HttpProtocolParams.setContentCharset(params,  
                    CHARSET);  
            HttpProtocolParams.setUseExpectContinue(params, true);  
            HttpProtocolParams  
                    .setUserAgent(  
                            params,  
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "  
                                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            
            
            // 超时设置  
            /* 从连接池中取连接的超时时间 */  
            ConnManagerParams.setTimeout(params, 1000);  
            /* 连接超时 */  
            HttpConnectionParams.setConnectionTimeout(params, 2000);  
            /* 请求超时 */  
            HttpConnectionParams.setSoTimeout(params, 4000);  
   
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式  
            SchemeRegistry schReg = new SchemeRegistry();  
            schReg.register(new Scheme("http", PlainSocketFactory  
                    .getSocketFactory(), 80));  
            schReg.register(new Scheme("https", SSLSocketFactory  
                    .getSocketFactory(), 443));  
   
            // 使用线程安全的连接管理来创建HttpClient  
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(  
                    params, schReg);  
            customerHttpClient = new DefaultHttpClient(conMgr, params);  
        }  
        return customerHttpClient;  
    }

    
    public static String post(String url, NameValuePair... params) throws ClientProtocolException, IOException   {
            
                // 编码参数
                List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
                for (NameValuePair p : params) {
                    formparams.add(p);
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
                        CHARSET);
                // 创建POST请求
                HttpPost request = new HttpPost(host+url);
                request.setEntity(entity);
                
                // 发送请求
                HttpClient client = getHttpClient();
                // 设置登陆用的sessionId, 不用每次都登陆
                if(null != sessionId){  
                    //((HttpMessage) customerHttpClient).setHeader("Cookie", "JSESSIONID=" + sessionId);
                    request.addHeader("Cookie", "JSESSIONID=" + sessionId+ "; rememberMe="+rememberMe);
                    if(rememberMe==null){
                        //需要重新登录
                    }
                    Log.d(TAG, "JSESSIONID=" + sessionId+ "; rememberMe="+rememberMe);
                }   
                
                HttpResponse response = client.execute(request);
                
                if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("请求失败");
                }
                updateSessionId();
                HttpEntity resEntity =  response.getEntity();
                return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
            
     
        }
}
