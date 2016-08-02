package com.eeda123.ui.gateOut;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eeda123.R;



public class SearchResultAdapter extends ArrayAdapter<SearchResultItem> {
    
    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
    
	private int resourceId;

	public SearchResultAdapter(Context context, int textViewResourceId,
			List<SearchResultItem> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchResultItem searchResultItem = getItem(position); // 获取当前项的实例
		View view;
		ViewHolder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder = new ViewHolder();

			viewHolder.gateOutOrderNo = (TextView) view.findViewById(R.id.order_no);
			viewHolder.waveOrderNo = (TextView) view.findViewById(R.id.status);
			viewHolder.salesOrderNo = (TextView) view.findViewById(R.id.customer);
			viewHolder.amount = (TextView) view.findViewById(R.id.plan_time);
			viewHolder.shelves = (TextView) view.findViewById(R.id.plan_time);
			viewHolder.seq = (TextView) view.findViewById(R.id.plan_time);
			view.setTag(viewHolder);
		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.gateOutOrderNo.setText(searchResultItem.getGateOutOrderNo());
		viewHolder.waveOrderNo.setText(searchResultItem.getWaveOrderNo());
		viewHolder.salesOrderNo.setText(searchResultItem.getSalesOrderNo());
		viewHolder.amount.setText(String.valueOf(searchResultItem.getAmount()));
		viewHolder.shelves.setText(formatter.format(searchResultItem.getShelves()));
		viewHolder.seq.setText(formatter.format(searchResultItem.getSeq()));
		return view;
	}

	class ViewHolder {
		TextView gateOutOrderNo;
		TextView waveOrderNo;
		TextView salesOrderNo;
		TextView amount;
		TextView shelves;
		TextView seq;
	}
}
