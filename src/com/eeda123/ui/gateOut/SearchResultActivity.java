package com.eeda123.ui.gateOut;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.eeda123.R;

import com.eeda123.ui.base.BaseActivity;
import com.eeda123.utils.CommonTools;
import com.eeda123.utils.EedaHttpClient;
import com.eeda123.widgets.AutoClearEditText;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class SearchResultActivity extends BaseActivity {
    private List<SearchResultItem> searchResultList = new ArrayList<SearchResultItem>();

    SearchResultAdapter listAdapter = null;
    private ListView listView = null;
    private ImageButton mBackBtn = null;
    
    private String searchString = null;
    
    private int pageIndex = 0;
    private int totalAmount =0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_barcdoe_result);

        initSearchResult(); // 初始化查询结果
        findViewById();
        
        listAdapter = new SearchResultAdapter(
                SearchResultActivity.this, R.layout.search_barcode_result_item,
                searchResultList);
        
        listView.setAdapter(listAdapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnScrollListener(new OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                // 当不滚动时
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        if(listAdapter.getCount() < totalAmount){
                            ++pageIndex;
                            Toast.makeText(getApplicationContext(), "正在加载数据...", Toast.LENGTH_LONG).show();
                            loadNextPage();
                        }else{
                            Toast.makeText(getApplicationContext(), "没有更多数据了", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            }
        });

        
        initView();
    }

    private void initSearchResult() {
        Intent intent = getIntent();
        searchString = intent.getStringExtra("searchString");
        
        
        List dtoList  = (List) intent
                .getSerializableExtra("result_dto_list");
        totalAmount = dtoList.size();

        Toast.makeText(getApplicationContext(), "共"+totalAmount+"条记录", Toast.LENGTH_LONG).show();
        for (Object obj : dtoList) {
            Map order = (HashMap)obj;
            SearchResultItem item = new SearchResultItem(
                    (String) order.get("SALES_ORDER_NO"),
                    (String) order.get("SALES_ORDER_NO"),
                    (String) order.get("SALES_ORDER_NO"),
                    (Double) order.get("AMOUNT"), 
                    (String) order.get("SHELVES"),
                    (Integer) order.get("SHELVES")
            );
            searchResultList.add(item);
        }

    }

    @Override
    protected void findViewById() {
        mBackBtn = (ImageButton) findViewById(R.id.back_btn);
        listView = (ListView) findViewById(R.id.search_list);
    }

    @Override
    protected void initView() {
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void loadNextPage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    NameValuePair param1 = new BasicNameValuePair("pageIndex", String.valueOf(pageIndex));
                    
                    String url = "/m/searchOrder/"+searchString;
                    String response =  EedaHttpClient.post(url, param1);
                    Log.i("eeda", response);

                    Message message = new Message(); 
                    message.what = 0;
                    // 将服务器返回的结果存放到Message中
                    message.obj = response.toString();
                    handler.sendMessage(message);
                   
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String responseJson = (String) msg.obj; // 在这里进行UI操作,将结果显示到界面上
            Gson gson = new Gson();
            Map<String, ?> dto = gson.fromJson(responseJson, HashMap.class);
            for (Object obj : (List)dto.get("orderList")) {
                Map order = (LinkedTreeMap<String, ?>)obj;
                SearchResultItem item = new SearchResultItem(
                        (String) order.get("ORDER_NO"),
                        (String) order.get("CUSTOMER_NAME"),
                        (String) order.get("STATUS"), 
                        (Integer) order.get("STATUS"),
                        (String) order.get("STATUS"),
                        (Integer) order.get("STATUS"));
                searchResultList.add(item);
            }
            listAdapter.notifyDataSetChanged();//刷新UI
        }
    };
}
