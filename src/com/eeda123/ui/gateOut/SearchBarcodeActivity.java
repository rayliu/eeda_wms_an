package com.eeda123.ui.gateOut;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import com.eeda123.ui.gateOut.SearchResultActivity;
import com.eeda123.utils.CommonTools;
import com.eeda123.utils.EedaHttpClient;
import com.eeda123.widgets.AutoClearEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SearchBarcodeActivity extends BaseActivity {

    private AutoClearEditText mEditText = null;
    private Button mSearchButton = null;
    private ImageButton mBackButton = null;
    private Intent mIntent;
    private ProgressBar progressBar;
    
    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            mEditText.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra("barocode");
            //byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);

            mEditText.setText(barcodeStr);
            if(mScanManager!=null)
              mScanManager.stopDecode();
        }

    };
    
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
            List dtoList = gson.fromJson(responseJson, List.class);
            
            int totalAmount = dtoList.size();
            if(totalAmount==0){
//                CommonTools.showShortToast(SearchTransferOrderActivity.this,
//                     "抱歉，没有找到相关记录。");
                Toast.makeText(getApplicationContext(), "抱歉，没有找到相关记录。", Toast.LENGTH_LONG).show();
            }else{//跳到结果列表
//                CommonTools.showShortToast(SearchTransferOrderActivity.this,
//                        "找到相关记录"+totalAmount+"条");
                mIntent=new Intent(SearchBarcodeActivity.this, SearchResultActivity.class);
                mIntent.putExtra("result_dto_list", (Serializable)dtoList);
                mIntent.putExtra("searchString", mEditText.getText().toString());
                startActivity(mIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_barcode);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        findViewById();
        initView();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        mEditText.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        unregisterReceiver(mScanReceiver);
    }
    @Override
    protected void findViewById() {
        mEditText = (AutoClearEditText) findViewById(R.id.order_no);

        mSearchButton = (Button) findViewById(R.id.search_btn);
        mSearchButton.setOnClickListener(indexClickListener);
        
//        mSearchButton = (Button) findViewById(R.id.search_btn);
//        mSearchButton.setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                //if(type == 3)
//                    mScanManager.stopDecode();
//                    isScaning = true;
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    mScanManager.startDecode();
//            }
//        });

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
                    CommonTools.showShortToast(SearchBarcodeActivity.this,
                         "请输入条码");
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

    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner(); 
      
        mScanManager.switchOutputMode( 0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }
    //发送请求到后台
    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message(); 
                try {
                    String url = "/m/searchBarcode/"+mEditText.getText();
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
