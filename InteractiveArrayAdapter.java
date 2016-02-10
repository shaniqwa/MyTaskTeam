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
		protected TextView location;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.task_cell, null);
			final ViewHolder viewHolder = new ViewHolder();
			//define the data in a task cell
			viewHolder.text = (TextView) view.findViewById(R.id.textCategory);
			viewHolder.subText = (TextView) view.findViewById(R.id.textDes);
			viewHolder.dueDate = (TextView) view.findViewById(R.id.textDue);
			viewHolder.status =  (TextView) view.findViewById(R.id.textStatus);
			viewHolder.location = (TextView) view.findViewById(R.id.textLocation);

			//Set the typeface
//			Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf" );
//			viewHolder.status.setTypeface(font);

			JSONObject obj = list.get(position);

			try {
				viewHolder.text.setText(obj.getString("cat"));
				viewHolder.subText.setText(obj.getString("des"));
				viewHolder.dueDate.setText(obj.getString("dueTime"));
				viewHolder.location.setText(obj.getString("location"));
				String status = obj.getString("status");
				switch (status){
					case "0" :
						viewHolder.status.setText(context.getResources().getString(R.string.status_no_res));
						viewHolder.status.setTextColor(Color.BLUE);
						break;
					case "1" :
						viewHolder.status.setText(context.getResources().getString(R.string.status_accept_waiting));
						break;
					case "2" :
						viewHolder.status.setText(context.getResources().getString(R.string.status_accept_in_process));
						break;
					case "3" :
						viewHolder.status.setText(context.getResources().getString(R.string.status_accept_done));
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

		}
		return view;
	}
}