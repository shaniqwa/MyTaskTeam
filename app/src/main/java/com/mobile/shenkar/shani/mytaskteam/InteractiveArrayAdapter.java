package com.mobile.shenkar.shani.mytaskteam;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class InteractiveArrayAdapter extends ArrayAdapter<JSONObject> {

	private final List<JSONObject> list;
	private final Activity context;

	public InteractiveArrayAdapter(Activity context, List<JSONObject> list) {
		super(context, R.layout.task_cell, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView subText;
		protected TextView text;
		protected TextView dueDate;
		protected TextView status;
		protected TextView priority;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder viewHolder;

		if (convertView == null){
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.task_cell, null);
		}
		else {
			view = convertView;
		}


		viewHolder = new ViewHolder();
		//define the data in a task cell
		viewHolder.text = (TextView) view.findViewById(R.id.textCategory);
		viewHolder.subText = (TextView) view.findViewById(R.id.textDes);
		viewHolder.dueDate = (TextView) view.findViewById(R.id.textDue);
		viewHolder.status =  (TextView) view.findViewById(R.id.textStatus);
		viewHolder.priority = (TextView) view.findViewById(R.id.textPriority);

		try {
			JSONObject obj = list.get(position);

			String priority = obj.getString("priority");
			switch(priority){
				case "1" :
					viewHolder.priority.setText("Priority: Low");
					break;
				case "2" :
					viewHolder.priority.setText("Priority: Normal");
					break;
				case "3" :
					viewHolder.priority.setText("Priority: Urgent");
			}

			//todo: set background only for members
			if(obj.getString("new").compareTo("0")== 0 ){
				view.setBackgroundResource(R.color.colorPrimaryDark); //todo: fade after few seconds
			}


			viewHolder.text.setText(obj.getString("cat"));
			viewHolder.subText.setText(obj.getString("des"));
			viewHolder.dueDate.setText(obj.getString("dueTime"));

			String status = obj.getString("status");
			switch (status){
				case "0" :
					viewHolder.status.setText(context.getResources().getString(R.string.status_no_res));
					viewHolder.status.setTextColor(Color.BLUE);
					break;
				case "1" :
					viewHolder.status.setText(context.getResources().getString(R.string.status_accept_waiting));
					viewHolder.status.setTextColor(Color.parseColor("#44b675"));
					break;
				case "2" :
					viewHolder.status.setText(context.getResources().getString(R.string.status_accept_in_process));
					viewHolder.status.setTextColor(Color.parseColor("#44b675"));
					break;
				case "3" :
					viewHolder.status.setText(context.getResources().getString(R.string.status_accept_done));
					viewHolder.status.setTextColor(Color.parseColor("#44b675"));
					break;
				case "4" :
					viewHolder.status.setText(context.getResources().getString(R.string.status_reject));
					viewHolder.status.setTextColor(Color.RED);
					break;
			}
			viewHolder.status.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return view;
	}

}