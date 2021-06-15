package com.wazedrifat.myapplication.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wazedrifat.myapplication.Data;
import com.wazedrifat.myapplication.R;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.MyViewHolder>{
	ArrayList <Data> dataList;

	public myAdapter(ArrayList<Data> dataList) {
		this.dataList = dataList;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		holder.date.setText(dataList.get(position).getTime().toString());
		holder.value.setText(dataList.get(position).getValue());
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	class MyViewHolder extends RecyclerView.ViewHolder {
		TextView date, value;
		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			date = itemView.findViewById(R.id.itemDateID);
			value = itemView.findViewById(R.id.itemValueID);
		}
	}
}
