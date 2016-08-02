package com.eeda123.ui.transferOrder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eeda123.R;
import com.eeda123.ui.CartActivity;
import com.eeda123.ui.base.BaseActivity;
import com.eeda123.utils.CommonTools;
import com.eeda123.utils.EedaHttpClient;
import com.eeda123.widgets.AutoClearEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SearchTransferOrderActivity extends BaseActivity {

    private AutoClearEditText mEditText = null;
    private Button mSearchButton = null;
    private ImageButton mBackButton = null;
    private Intent mIntent;
    private ProgressBar progressBar;
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.INVISIBLE);
            if(msg.what == 1){//err
                Toast.makeText(getApplicationContext(), "出错:"+(String) msg.obj, Toast.LENGTH_LONG).show();
                return;
            }
            String responseJson = (String) msg.obj; // 在这里进行UI操作,将结果显示到界面上
            
            //TODO: 这里应该有公用的方法，不需要每次单独写
            if(responseJson.indexOf("下次自动登录")>0){//登陆不成功
                Toast.makeText(getApplicationContext(), "查询失败，您需要重新登录系统", Toast.LENGTH_LONG).show();
                return;
            }
            
            
            Gson gson = new Gson();
            Map<String, ?> dto = gson.fromJson(responseJson, HashMap.class);
            
            int totalAmount = ((Double)dto.get("iTotalRecords")).intValue();
            if(totalAmount==0){
//                CommonTools.showShortToast(SearchTransferOrderActivity.this,
//                     "抱歉，没有找到相关记录。");
                Toast.makeText(getApplicationContext(), "抱歉，没有找到相关记录。", Toast.LENGTH_LONG).show();
            }else{//跳到结果列表
//                CommonTools.showShortToast(SearchTransferOrderActivity.this,
//                        "找到相关记录"+totalAmount+"条");
                mIntent=new Intent(SearchTransferOrderActivity.this, SearchResultActivity.class);
                mIntent.putExtra("result_dto", (Serializable)dto);
                mIntent.putExtra("searchString", mEditText.getText().toString());
                startActivity(mIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transfer_order);

        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        mEditText = (AutoClearEditText) findViewById(R.id.order_no);

        mSearchButton = (Button) findViewById(R.id.search_btn);
        mSearchButton.setOnClickListener(indexClickListener);

        mBackButton = (ImageButton) findViewById(R.id.back_btn);
        mBackButton.setOnClickListener(indexClickListener);
        
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void initView() {
        mEditText.requestFocus();
    }

    private OnClickListener indexClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.search_btn:
                String orderNo = mEditText.getText().toString();
                if(orderNo.trim().length()==0){
                    CommonTools.showShortToast(SearchTransferOrderActivity.this,
                         "请输入单号");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    sendRequestWithHttpClient();
                }
                break;
            default:
                break;
            }

        }
    };

    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message(); 
                try {
                    String url = "/m/searchOrder/"+mEditText.getText();
                    String response =  EedaHttpClient.post(url);
                    Log.i("eeda", response);
                   
                    message.what = 0;
                    // 将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    handler.sendMessage(message);
                   
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = 1;
                    message.obj = e.getMessage();
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

}
