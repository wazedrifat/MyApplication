package com.wazedrifat.myapplication.ui.gallery;

import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wazedrifat.myapplication.Data;
import com.wazedrifat.myapplication.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
	final String FIRESTOR_READ_TAG = "firestore_read";
	private GalleryViewModel galleryViewModel;

	private RecyclerView recyclerView;
	ArrayList <Data> dataList;
	myAdapter adapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		galleryViewModel =
				new ViewModelProvider(this).get(GalleryViewModel.class);
		View root = inflater.inflate(R.layout.fragment_gallery, container, false);

		recyclerView = root.findViewById(R.id.recycleViewID);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		dataList = new ArrayList<Data>();
		adapter = new myAdapter(dataList);
		recyclerView.setAdapter(adapter);


		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users")
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : task.getResult()) {
//								Log.d(FIRESTOR_READ_TAG, document.getId() + " => " + document.getData());
								dataList.add(document.toObject(Data.class));
							}
						} else {
//							Log.w(FIRESTOR_READ_TAG, "Error getting documents.", task.getException());
						}

						adapter.notifyDataSetChanged();
					}
				});


		return root;
	}
}