package com.wazedrifat.myapplication.ui.slideshow;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wazedrifat.myapplication.R;
import com.wazedrifat.myapplication.jsonData;

import java.util.ArrayList;

public class myAdapterJson extends RecyclerView.Adapter<myAdapterJson.MyViewHolderJson>{
	ArrayList<jsonData> dataList;

	public myAdapterJson(ArrayList<jsonData> dataList) {
		this.dataList = dataList;
	}

	@NonNull
	@Override
	public myAdapterJson.MyViewHolderJson onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.json_item, parent, false);

		return new myAdapterJson.MyViewHolderJson(view);
	}

	@SuppressLint("SetTextI18n")
	@Override
	public void onBindViewHolder(@NonNull myAdapterJson.MyViewHolderJson holder, int position) {
		holder.userid.setText("User ID : " + String.valueOf(dataList.get(position).getUserId()));
		holder.id.setText("ID : " + String.valueOf(dataList.get(position).getId()));
		holder.title.setText("Title : " + dataList.get(position).getTitle());
		holder.body.setText("Body : " + dataList.get(position).getBody());
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	class MyViewHolderJson extends RecyclerView.ViewHolder {
		TextView userid, id, title, body;
		public MyViewHolderJson(@NonNull View itemView) {
			super(itemView);
			userid = itemView.findViewById(R.id.userID);
			id = itemView.findViewById(R.id.ID);
			title = itemView.findViewById(R.id.titleID);
			body = itemView.findViewById(R.id.bodyID);
		}
	}
}

