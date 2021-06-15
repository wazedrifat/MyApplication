package com.wazedrifat.myapplication.ui.slideshow;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.wazedrifat.myapplication.Data;
import com.wazedrifat.myapplication.R;
import com.wazedrifat.myapplication.jsonData;
import com.wazedrifat.myapplication.ui.gallery.myAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

	private SlideshowViewModel slideshowViewModel;
	private RecyclerView recyclerView;
	ArrayList<jsonData> dataList;
	myAdapterJson adapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		slideshowViewModel =
				new ViewModelProvider(this).get(SlideshowViewModel.class);
		View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

		recyclerView = root.findViewById(R.id.jsonRecycleViewID);

		dataList = new ArrayList<jsonData>();

		parseJson();

		return root;
	}

	private void parseJson() {
		RequestQueue queue = Volley.newRequestQueue(getActivity());
		String JSON_URL = "https://jsonplaceholder.typicode.com/posts";
		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				for (int i = 0; i < response.length(); i++) {
					try {
						JSONObject object = response.getJSONObject(i);
						jsonData data = new jsonData();
						data.setUserId(object.getInt("userId"));
						data.setId(object.getInt("id"));
						data.setTitle(object.getString("title"));
						data.setBody(object.getString("body"));
						dataList.add(data);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
				adapter = new myAdapterJson(dataList);
				recyclerView.setAdapter(adapter);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});

		queue.add(jsonArrayRequest);
	}
}