package com.eeda123.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eeda123.R;
import com.eeda123.adapter.SearchResultAdapter;
import com.eeda123.entity.SearchResultItem;
import com.eeda123.ui.base.BaseActivity;
import com.eeda123.utils.CommonTools;
import com.eeda123.widgets.AutoClearEditText;


public class SearchActivity extends BaseActivity {
	private List<SearchResultItem> searchResultList = new ArrayList<SearchResultItem>();
	
	private AutoClearEditText mEditText = null;
	private ImageButton mImageButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		initSearchResult(); // 初始化查询结果
		SearchResultAdapter adapter = new SearchResultAdapter(SearchActivity.this,
				R.layout.search_result_item, searchResultList);
		ListView listView = (ListView) findViewById(R.id.search_list); 
		listView.setAdapter(adapter);
		listView.setVisibility(View.VISIBLE);
		
		findViewById();
		initView();
	}

	private void initSearchResult() {
		SearchResultItem apple = new SearchResultItem("YS2015092200001","", "New", new Date(), R.drawable.android_personel_all_order); 
		searchResultList.add(apple);
		SearchResultItem banana = new SearchResultItem("YS2015092200002", "", "New", new Date(), R.drawable.android_personel_all_order); 
		searchResultList.add(banana);
		SearchResultItem orange = new SearchResultItem("YS2015092200003", "", "New", new Date(), R.drawable.android_personel_all_order); 
		searchResultList.add(orange);
	}
	
	@Override
	protected void findViewById() {
		mEditText = (AutoClearEditText) findViewById(R.id.search_edit);
		mImageButton = (ImageButton) findViewById(R.id.search_button);
	}

	@Override
	protected void initView() {
		mEditText.requestFocus();
		mImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonTools.showShortToast(SearchActivity.this, "亲，该功能暂未开放");
			}
		});
	}
}
