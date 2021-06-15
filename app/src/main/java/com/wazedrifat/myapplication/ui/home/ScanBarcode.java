package com.wazedrifat.myapplication.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.wazedrifat.myapplication.Data;
import com.wazedrifat.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanBarcode extends AppCompatActivity {

	private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
	private ExecutorService cameraExecutor;
	private PreviewView previewView;
	private  MyImageAnalyzer analyzer;
	FirebaseDatabase database;
	DatabaseReference myRef;
	List<Data> dataList;
	FirebaseFirestore db;
	private Date time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_barcode);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR,  - 1);
		time = cal.getTime();

		db = FirebaseFirestore.getInstance();
		previewView = findViewById(R.id.previewID);
		this.getWindow().setFlags(1024, 1024);

		cameraExecutor = Executors.newSingleThreadExecutor();
		cameraProviderFuture = ProcessCameraProvider.getInstance(this);

		analyzer = new MyImageAnalyzer(getSupportFragmentManager());

		dataList = new ArrayList<Data>();

		cameraProviderFuture.addListener(new Runnable() {
			@Override
			public void run() {
				try {
					if (ActivityCompat.checkSelfPermission(ScanBarcode.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(ScanBarcode.this, new String[]{Manifest.permission.CAMERA}, 101);
					}
					else {
						ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
						bindpreview(processCameraProvider);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}, ContextCompat.getMainExecutor(this));

//		database = FirebaseDatabase.getInstance();
//		myRef = database.getReference("message");


//		myRef.addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				// This method is called once with the initial value and again
//				// whenever data at this location is updated.
//				 = dataSnapshot.getValue(List.class);
//
//			}
//
//			@Override
//			public void onCancelled(@NonNull DatabaseError error) {
//
//			}
//		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == 101 && grantResults.length > 0) {
			ProcessCameraProvider processCameraProvider = null;
			try {
				processCameraProvider = cameraProviderFuture.get();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
			bindpreview(processCameraProvider);
		}
	}

	private void bindpreview(ProcessCameraProvider processCameraProvider) {
		Preview preview = new Preview.Builder().build();
		CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
		preview.setSurfaceProvider(previewView.getSurfaceProvider());
		ImageCapture imageCapture = new ImageCapture.Builder().build();
		ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
				.setTargetResolution(new Size(1280, 720))
				.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
				.build();
		imageAnalysis.setAnalyzer(cameraExecutor, analyzer);
		processCameraProvider.unbindAll();
		processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
	}

	public class MyImageAnalyzer implements ImageAnalysis.Analyzer {
		private FragmentManager fragmentManager;

		public MyImageAnalyzer(FragmentManager fragmentManager) {
			this.fragmentManager = fragmentManager;
		}

		@Override
		public void analyze(@NonNull ImageProxy image) {
			scanbarcode(image);
		}
	}

	private void scanbarcode(ImageProxy image) {
		@SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
		assert  image1 != null;

		InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());
		BarcodeScannerOptions barcodeScannerOptions = new BarcodeScannerOptions.Builder()
				.setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();

		BarcodeScanner scanner = BarcodeScanning.getClient(barcodeScannerOptions);

		Task<List<Barcode>> result = scanner.process(inputImage)
				.addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
					@RequiresApi(api = Build.VERSION_CODES.O)
					@Override
					public void onSuccess(List<Barcode> barcodes) {
						// Task completed successfully
						try {
							readBarcodeData(barcodes);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						// Task failed with an exception
						// ...
					}
				}).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
					@Override
					public void onComplete(@NonNull Task<List<Barcode>> task) {
						image.close();
					}
				});
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void readBarcodeData(List<Barcode> barcodes) throws ParseException {
		Data data = null;
		for (Barcode barcode: barcodes) {
			Rect bounds = barcode.getBoundingBox();
			Point[] corners = barcode.getCornerPoints();

			String rawValue = barcode.getRawValue();

			HomeFragment.res.setText(rawValue);
			data = new Data(rawValue);


//			dataList.add(new Data(rawValue));
//			myRef.setValue(dataList);


//			int valueType = barcode.getValueType();
			// See API reference for complete list of supported types
//			switch (valueType) {
//				case Barcode.TYPE_WIFI:
//					String ssid = barcode.getWifi().getSsid();
//					String password = barcode.getWifi().getPassword();
//					int type = barcode.getWifi().getEncryptionType();
//					break;
//				case Barcode.TYPE_URL:
//					String title = barcode.getUrl().getTitle();
//					String url = barcode.getUrl().getUrl();
//					break;
//			}
		}


		if (data != null) {
			Duration d = Duration.between(data.getTime().toInstant(), time.toInstant());
			Log.d("myTime", String.valueOf(d.getSeconds() > 2));
			Log.d("myTime", String.valueOf(d.getSeconds()));
			if (Math.abs(d.getSeconds()) > 2) {
				time = Calendar.getInstance().getTime();

				db.collection("users")
						.add(data)
						.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
							@Override
							public void onSuccess(DocumentReference documentReference) {
								Log.d("firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
							}
						})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.w("firestore", "Error adding document", e);
							}
						});
			}
			finish();
		}
	}
}