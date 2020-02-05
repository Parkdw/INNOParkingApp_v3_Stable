/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.innocns.innoparking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.entereal.android.BackPressCloseHandler;
import com.entereal.android.ParkingManager;
import com.entereal.android.Web;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;


import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.widget.LocationButtonView;


import com.skt.Tmap.TMapTapi;
import com.tsengvn.typekit.TypekitContextWrapper;

/*import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;*/
//import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/* NaverMap V3 2020.02.04 */
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.MapView;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.util.FusedLocationSource;

import android.support.v7.app.AppCompatActivity;


public class NMapViewer extends AppCompatActivity implements OnMapReadyCallback {

	/* NaverMap V3 2020.02.05 */
	// 안드로이드 ViewGroup 클래스를 상속받은 클래스로서 지도 데이터를 화면에 표시
	private MapFragment mapFragment;
	private CameraUpdate cameraUpdate;
	// NaverMap API 3.0
	private MapView mapView;
	private LocationButtonView locationButtonView;
	private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
	private FusedLocationSource locationSource;
	NaverMap mnaverMap;
	Marker marker;
	CameraPosition cameraPosition;


	public static NMapViewer instance;
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your Client ID which is registered for NMapViewer library.
	private static final String NAVER_CLIENT_ID = "5z3fbsprqu";		// "HJK8jeLufAFhJmloOiZM"; 테스트 개인 계정
	private static final String TMAP_APPKEY = "b872aab5-c8e8-408c-b3b4-2eb096343d0f"; // "f6a58fed-3b68-3414-b407-74c86546209a"; 테스트 개인 계정

	private MapContainerView mMapContainerView;

	// 안드로이드 ViewGroup 클래스를 상속받은 클래스로서 지도 데이터를 화면에 표시
	//private NMapView mMapView;

	//지도의 상태를 변경하고 컨트롤하기 위한 클래스 지도 중심 및 축척 레벨 변경과 지도 확대, 축소, 패닝 등 다양한 기능을 수행
	//private NMapController mMapController;

	/* NaverMap V3 2020.02.04 */
	//private NMapFragment m3MapFragment;		//	2020.02.04
	private MapView m3MapView;		//	2020.02.04
    private NaverMap m3NaverMap;
    private LocationSource myLocationSource;

	// 지도 상의 경위도 자표를 나타내는 클래스
	private static final LatLng NMAP_LOCATION_DEFAULT = new LatLng(126.978415, 37.566667); // 위도, 경도순

	private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
	// 일반 지도 보기 모드
	//private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

	private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

	private SharedPreferences mPreferences;

	private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;



//	// 지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속
//	private NMapMyLocationOverlay mMyLocationOverlay;
//	// 단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스입니다.
//	private NMapLocationManager mMapLocationManager;
//	// 단말기의 나침반 기능을 사용하기 위한 클래스입니다.
//	private NMapCompassManager mMapCompassManager;
	//LocationOverlay locationOverlay = mnaverMap.getLocationOverlay();
	//locationOverlay.setVisible(true);

	
	
	// 평소에는 화면의 한쪽에 숨겨져 있다가 사용자가 액션을 취하면 화면에 나타나는 기능을 만들 수 있게 해주는 레이아웃
	DrawerLayout dlDrawer;
	// 네비게이션 메뉴를 위한 버튼
	ActionBarDrawerToggle drawerToggle;
	View drawerView;
	EditText editTextSearch;

	ListView listViewSliderParking;

	TMapTapi tmaptapi;

//	private NMapCalloutCustomOverlayView selectedOverlayView;

	private static boolean USE_XML_LAYOUT = true;
	private boolean isFirst = true;
	private BackPressCloseHandler backPressCloseHandler;
	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

	public static LatLng target_Point;		// 근처 장애인 주차장 Point
	public static LatLng my_Location;		// 현재위치 Point

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

//		if (USE_XML_LAYOUT) {
//			setContentView(R.layout.main);
//			setProgressBarIndeterminateVisibility(false);
//
//			//mMapView = (NMapView)findViewById(R.id.mapView);	// v3 update 2020.02.05
//			mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
//
//		} else {
//			/* create map view */
//			//mMapView = new NMapView(this);	// v3 update 2020.02.05
//			mapFragment = new MapFragment();
//
//			/* create parent view to rotate map view */
//			mMapContainerView = new MapContainerView(this);
//			mMapContainerView.addView(mapFragment);
//
//
//			// set the activity content to the parent view
//			setContentView(mapFragment);
//		}
		setContentView(R.layout.main);
		setProgressBarIndeterminateVisibility(false);
		mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
		if (mapFragment == null) {
			mapFragment = MapFragment.newInstance();
			getSupportFragmentManager().beginTransaction().add(R.id.mapView, mapFragment).commit();
		}
		mapFragment.getMapAsync(NMapViewer.this);


		// Open MapViewer 라이브러리에 대해 등록 된 클라이언트 ID를 설정합니다.
		NaverMapSdk.getInstance(this).setClient(
				new NaverMapSdk.NaverCloudPlatformClient(NAVER_CLIENT_ID));

//		// map view 초기화
//		mMapView.setClickable(true);
//		mMapView.setEnabled(true);
//		mMapView.setFocusable(true);
//		mMapView.setFocusableInTouchMode(true);
//		mMapView.requestFocus();



//		// register listener for map state changes
//		//지도 상태 변경시 호출되는 콜백 인터페이스를 설정
//		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
//		// 지도에서 터치 이벤트 처리 후 호출되는 콜백 인터페이스를 설정
//		mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
//		mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);
//
//		// 지도 컨트롤러를 사용하여 확대 / 축소, 회전 및 확대지도 센터, 확대 / 축소 수준 등
//		// 지도 컨트롤 객체를 가져옴
//		mMapController = mMapView.getMapController();
//		mMapController.setZoomLevelConstraint(1, 14);
//
//		// 내장 된 줌 컨트롤 사용
//		NMapView.LayoutParams lp = new NMapView.LayoutParams(LayoutParams.WRAP_CONTENT,
//				LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
//		// 내장된 줌 컨트롤의 활성화 여부를 설정한다. 내장된 줌 컨트롤의 위치는 lp로 조정 가능하며 null로 전달하면 기본 위치에 표시된다.
//		mMapView.setBuiltInZoomControls(true, lp);
//
//		// set data provider listener
//		super.setMapDataProviderListener(onDataProviderListener);
//


//
//		// location manager
//		// 단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스
//		mMapLocationManager = new NMapLocationManager(this);
//		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);
//
//		// compass manager
//		// 단말기의 나침반 기능을 사용하기 위한 클래스
//		mMapCompassManager = new NMapCompassManager(this);
//
//		// create my location overlay
//		// 현재 위치 및 나침반 관리자를 인자로 전달하여 NMapMyLocationOverlay 객체를 생성한다. compassManager를 null로 전달하면 현재 위치만 표시한다.
//		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

		// 툴바 아이콘
		getSupportActionBar().setIcon(R.drawable.ic_main_search);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// DrawerLayout 생성
		dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		// 뷰생성
		drawerView = (View)findViewById(R.id.drawer);
		// 텍스트 생성
		editTextSearch = (EditText)findViewById(R.id.textSearch);
		// 텍스트 스타일 설정
		editTextSearch.setTypeface(Typeface.DEFAULT);


		// item 나열 리스트 뷰 생성
		listViewSliderParking = (ListView)findViewById(R.id.lv_activity_main_nav_list);

		// item 나열리스트 클릭 이벤트
		listViewSliderParking.setOnItemClickListener(new DrawerItemClickListener());

		// item 검색 이벤트
		drawerView.findViewById(R.id.btn_search).setOnClickListener(onClickSearchListener);


		// 새로운 ActionBarDrawerToggle를 구축
		drawerToggle = new ActionBarDrawerToggle(this, dlDrawer, null, R.string.empty, R.string.empty) {

			/** 완전히 닫힌 상태로 안정화 될 때 호출됩니다. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
			}

			/**  완전히 열린 상태로 안정화 될 때 호출됩니다. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
				if(editTextSearch.getText().toString().equals("")) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					//imm.showSoftInput(editTextSearch, InputMethodManager.SHOW_FORCED);
					//imm.showSoftInputFromInputMethod(editTextSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
					imm.toggleSoftInputFromWindow(editTextSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
				}
			}
		};

		//drawer 이벤트의 통지를받는 리스트에 추가
		dlDrawer.addDrawerListener(drawerToggle);

		tmaptapi = new TMapTapi(this);
		tmaptapi.setSKTMapAuthentication(TMAP_APPKEY);
		Log.i(LOG_TAG,"tamp api key"+TMAP_APPKEY);
		tmaptapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
			@Override
			public void SKTMapApikeySucceed() {
				Log.i(LOG_TAG, "tamp success");
			}

			@Override
			public void SKTMapApikeyFailed(String s) {
				Log.i(LOG_TAG, "tamp failed" + " " + s);
			}
		});

		// 뒤로가기 클래스 호출
		backPressCloseHandler = new BackPressCloseHandler(this);

//		// 내장된 줌 컨트롤을 화면에 표시한다. 내장된 줌 컨트롤을 활성화 후에 사용 가능
//		mMapView.displayZoomControls(false);
//		// 지도 최대 축척 레벨인 14레벨 지원 여부를 설정
//		setHighestZoomLevelEnabled(true);
		setProgressBarIndeterminateVisibility(false);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		naverMapBasicSettings();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			/* (GPS OFF)	||		(GPS ON & 위치 권한 거부) */
			//mMapController.setMapCenter(NMAP_LOCATION_DEFAULT);
			CameraUpdate.scrollAndZoomTo(NMAP_LOCATION_DEFAULT, 15);

			Toast.makeText(NMapViewer.this, "위치 권한 거부", Toast.LENGTH_LONG).show();
		}
		else {

			/* GPS ON & 위치권한 허용일 때 */

			Toast.makeText(NMapViewer.this, "위치 권한 허용 됨", Toast.LENGTH_LONG).show();

			//initSearchParkingLot();

		}

	}


	public void naverMapBasicSettings() {
		//내위치 버튼
		locationButtonView = findViewById(R.id.action_my_location);
		// 내위치 찾기 위한 source
		locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
	}


	@UiThread
	@Override
	public void onMapReady(@NonNull NaverMap naverMap) {
		naverMap.getUiSettings().setLocationButtonEnabled(true);
//		locationButtonView.setMap(naverMap);


		// UI 관련 설정 담당
		UiSettings uiSettings = naverMap.getUiSettings();
		// 위치 버튼 활성화
		uiSettings.setLocationButtonEnabled(true);


		// Location Change Listener을 사용하기 위한 FusedLocationSource 설정
		naverMap.setLocationSource(locationSource);
		naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


//		// 카메라 위치와 줌 조절(숫자가 클수록 확대)
//		CameraPosition cameraPosition = new CameraPosition(location, 17);
//		naverMap.setCameraPosition(cameraPosition);

		// 줌 범위 제한
		naverMap.setMinZoom(5.0);   //최소
		naverMap.setMaxZoom(18.0);  //최대

		// 카메라 영역 제한
		LatLng northWest = new LatLng(31.43, 122.37);
		LatLng southEast = new LatLng(44.35, 132);
		naverMap.setExtent(new LatLngBounds(northWest, southEast));


		/* MapView State Change Listener*/
		naverMap.addOnCameraChangeListener((reason, animated) -> {
			CameraPosition cameraPosition = naverMap.getCameraPosition();

			Log.i("NaverMap", "카메라 변경 - 위도: " + cameraPosition.target.latitude + ", " +
					"경도: " + cameraPosition.target.longitude + ", " +
					"줌 레벨: " + cameraPosition.zoom + ", " +
					"기울임 각도: " + cameraPosition.tilt );
		});
		//mnaverMap.getCameraPosition();


		loadParkingData(naverMap);
		Log.i("*** NaverMap", "Data Load Success!!");


	}


	/*private void initSearchParkingLot () {
		*//* 위치 변화 리스너 onMyLocationChangeListener 는 아래에 정의  (1100줄 정도에 정의) *//*
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);  // 위치변화 리스너 Set
	}*/


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//	/* 위치 변화 리스너 객체 생성 */
//	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
//
//		@Override
//		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
//
//			if (mMapController != null) {
//				mMapController.animateTo(myLocation);
//			}
//			Log.i("NEW", " ***** 현재 위치  :  " +  myLocation );
//			my_Location = myLocation;
//
///*  근처 주차장 검색 기능 OFF		2020.02.04 */
////			/* myLocation : 현재 좌표 */
////			List<ParkingManager.ParkingVO> list = ParkingManager.instance().all.list;
////			double[] comparedDist = new double[list.size()];
////			int min_idx = 0;
////			double radius_Distance = 5000.0;   //  주변 검색 반경 : 5km
////			double min = radius_Distance;
////
////			/*
////			// 현재좌표가 다른 지역일 때 TEST Points
////			NGeoPoint point_YangCheon1 = new NGeoPoint(126.861801, 37.516553);  // 양천 문화회관
////			NGeoPoint point_YangCheon2 = new NGeoPoint(126.874500, 37.526016);  // 양천 공영주차장
////			NGeoPoint point_Gwangneung_Arboretum = new NGeoPoint(127.182460, 37.751336);  // 광릉 수목원 주차장
////			*/
////
////			Log.i("result", "*****++++  list.size()  ++++******  " + list.size() );
////			if (list.size() > 0) {
////				for (int i = 0; i < list.size(); i++) {
////					ParkingManager.ParkingVO pv = list.get(i);
////					NGeoPoint to = new NGeoPoint(pv.lng, pv.lat);
////					comparedDist[i] = NGeoPoint.getDistance(myLocation, to);
////					//Log.i("comparedDist", "*********  comparedDist Data " + i + " : " + comparedDist[i]);
////
////					if (min > comparedDist[i]) {
////						min = comparedDist[i];
////						min_idx = i;
////					}
////				}
////				ParkingManager.ParkingVO targetData = list.get(min_idx);
////				target_Point = new NGeoPoint(targetData.lng, targetData.lat);
////
////				/* min data에 변화가 없다  ->  가까운 곳에 장애인 주차장이 없다는 것. */
////				if (radius_Distance == min){
////					Toast.makeText(NMapViewer.this, "가까운 장애인 주차장을 \n찾지 못했습니다...", Toast.LENGTH_LONG).show();
////
////					Log.i("result", "*****  가까운 장애인 주차장을 찾지 못했습니다...  *****  " );
////					mMapController.setMapCenter(myLocation);  // 본인 위치
////				}
////				else{
////					Log.i("result", "*****++++  Target_Parking_NAME  :  " + targetData.name +  " 주차장  ++++******  "  );
////					Log.i("result", "*****++++  Target_Parking_Point  :  " + target_Point +  "  ++++******  "  );
////
////					mMapController.setMapCenter(target_Point);  //  타겟 위치
////
////					Log.i("result", "*****  타켓 주차장으로 이동했습니다!!  *****  " );
////				}
////			}
////			else {
////				Toast.makeText(NMapViewer.this, " 주차장 데이터를 \n불러오지 못했습니다...", Toast.LENGTH_LONG).show();
////			}
//			return true;
//		}
//
//		// 정해진 시간 내에 현재 위치 탐색 실패 시 호출
//		@Override
//		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {
//
//			// stop location updating
//			//			Runnable runnable = new Runnable() {
//			//				public void run() {
//			//					stopMyLocation();
//			//				}
//			//			};
//			//			runnable.run();
//
//			Toast.makeText(NMapViewer.this, "지정 가능한 시간이 벗어났습니다.", Toast.LENGTH_LONG).show();
//		}
//
//		// 현재 위치가 지도 상에 표시할 수 있는 범위 벗어날때 호출
//		@Override
//		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
//
//			Toast.makeText(NMapViewer.this, "현재 위치를 표시할 수 없습니다.", Toast.LENGTH_LONG).show();
//
//			stopMyLocation();
//		}
//
//	};
//

	// 백 키 설정
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		if(dlDrawer.isDrawerOpen(drawerView)) {
			dlDrawer.closeDrawers();
			return;
		}
		backPressCloseHandler.onBackPressed();
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
	}

	// 검색 클릭 리스너
	private final View.OnClickListener onClickSearchListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if (onClickSearchListener != null) {
				Log.i(LOG_TAG, "search click");
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//				if(editTextSearch.getText().toString().equals("")) {
//					loadParkingSearchData("포천");
//				} else {
				// 검색 메서드
				loadParkingSearchData(editTextSearch.getText().toString());
//				}
			}
		}
	};

	// item 리스트 뷰 클릭 리스너
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
			Log.i(LOG_TAG, "position:" + position);
			ParkingManager.ParkingVO vo = ParkingManager.instance().search.list.get(position);


			InfoWindow infoWindow = new InfoWindow();
			infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(NMapViewer.this) {
				@NonNull
				@Override
				public CharSequence getText(@NonNull InfoWindow infoWindow) {
					return vo.name;
				}
			});
			LatLng marker_Position = new LatLng(vo.lng, vo.lat);
			marker.setPosition(marker_Position);
			marker.setTag(vo.name);

			marker.setMap(mnaverMap);
			marker.getInfoWindow();
			infoWindow.open(marker);

			dlDrawer.closeDrawers();
		}
	}

	// Activity가 사용자에게 보여지기 직전에 호출
	@Override
	protected void onStart() {
		super.onStart();

		if(isFirst) {
			isFirst = false;
			//Log.i("NEW", " ***** onStart() 현재 위치  :  " +  my_Location );

			//startMyLocation();


			// 지도 중심 좌표 및 축척 레벨을 설정한다. 축척 레벨을 지정하지 않으면 중심 좌표만 변경된다. 유효 축척 레벨 범위는 1~14이다.
			//mMapController.setMapCenter(127.48718, 34.95035);  //  순천시청
		}
	}



	// Activity가 사용자와 상호작용을 하기 직전에 호출
	@Override
	protected void onResume() {
		super.onResume();


	}

	// 다른 Activity가 Activity를 완전히 가려서 더 이상 보이지 않았을 때 호출
	@Override
	protected void onStop() {

		//stopMyLocation();

		super.onStop();
	}

	//  Activity가 삭제되지 직전에 호출
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/* Test Functions */
	// gps 설정
//	private void startMyLocation() {
//		if (mMyLocationOverlay != null) {
//			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
//				// 오버레이 추가
//				mOverlayManager.addOverlay(mMyLocationOverlay);
//			}
//
//			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//					!= PackageManager.PERMISSION_GRANTED) {
//				requestReadPhoneStatePermission();
//			}else{
//				doPermissionGrantedStuffs();
//			}
//			//Toast.makeText(NMapViewer.this, "테스트 준비 중에 있습니다....", Toast.LENGTH_LONG).show();
//		}
//	}

	private void requestReadPhoneStatePermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.ACCESS_FINE_LOCATION)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// For example if the user has previously denied the permission.
			new AlertDialog.Builder(this)
					.setTitle("GPS 권한 설정")
					.setMessage(getString(R.string.permission_read_phone_state_rationale))
					.setCancelable(false)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//re-request
							ActivityCompat.requestPermissions(NMapViewer.this,
									new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
									MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
						}
					})
					.setIcon(R.drawable.ic_warning_black_24dp)
					.show();
		} else {
			// READ_PHONE_STATE permission has not been granted yet. Request it directly.
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {

		if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
			// Received permission result for READ_PHONE_STATE permission.est.");
			// Check if the only required permission has been granted
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
				//alertAlert(getString(R.string.permision_available_read_phone_state));
				Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(goToSettings);
				
//				doPermissionGrantedStuffs();
				
			} else {
				alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
			}
		}
	}

	private void alertAlert(String msg) {
		new AlertDialog.Builder(NMapViewer.this)
				.setTitle("GPS 권한 설정")
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do somthing here
					}
				})
				.setIcon(R.drawable.ic_warning_black_24dp)
				.show();
	}

/*

	@SuppressLint("MissingPermission")
	public void doPermissionGrantedStuffs() {
		Boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
		// 현재 위치 탐색 중인지 여부를 반환한다.
		if (isMyLocationEnabled) {
			mMapView.postInvalidate();
			NGeoPoint myLocation = mMapLocationManager.getMyLocation();		// Map의 변경을 감지하면, 현재위치 값을 받아올 수 있다.
			//Log.i("NEW", " ***** doPermissionGrantedStuffs() 현재 위치  :  " +  myLocation );
		}
		else{	//현재 위치를 탐색 중이 아니면
			//Log.d("현재위치 실패","현재위치 실패");
		}
	}
	
*/
	
/*


	// 현재 위치 탐색을 종료한다.
	private void stopMyLocation() {
		if (mMyLocationOverlay != null) {
			mMapLocationManager.disableMyLocation();

			if (mMapView.isAutoRotateEnabled()) {
				mMyLocationOverlay.setCompassHeadingVisible(false);

				mMapCompassManager.disableCompass();

				mMapView.setAutoRotateEnabled(false, false);

				mMapContainerView.requestLayout();
			}
		}
	}

*/
	/* Naver Map V3 2020.02.05 */
	//콜백 메소드
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			//위치값 갱신시 이벤트발생
			//값은 location 형태로 리턴

			double latitude = location.getLatitude(); //위도
			double longitude = location.getLongitude(); //경도
			double altitude = location.getAltitude(); //고도
			float accuracy = location.getAccuracy(); //정확도
			String provider = location.getProvider(); //위치제공자
			// gps가 network위치보다 정확도가 좋다

			my_Location = new LatLng(location.getLatitude(), location.getLongitude());
			Log.i("*****", " In  onLocationChanged()  //  my_Location : " + my_Location);

			//updateMap(my_Location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//disable시
		}

		@Override
		public void onProviderEnabled(String provider) {
			//Enable시
		}

		@Override
		public void onProviderDisabled(String provider) {
			//변경시
		}
	};

	public void updateMap(LatLng latLng) {
		//오버레이 표시
		marker.setPosition(latLng);
		marker.setMap(mnaverMap);

		// 카메라 위치와 줌 조절 (숫자가 클수록 확대)
		cameraPosition = new CameraPosition(latLng, 17);
		mnaverMap.setCameraPosition(cameraPosition);
	}


	// 검색화면 열기
	private void showSearch() {
		Log.i(LOG_TAG, "showSearch");
		dlDrawer.openDrawer(drawerView);
	}
	

	private void loadParkingData(NaverMap naverMap) {
		//Web.VO vo = new Web.VO(Web.URL + "/api/parkinglot.php");
		//Web.VO vo = new Web.VO(Web.URL + "/beaconparking/api/LoadParkingLot.php"); // php
		Web.VO vo = new Web.VO(Web.URL + "/parkinglot/geocode/list.json"); // jsp
		Web.send(vo, new Web.Callback() {
			@Override
			public void callback(JSONObject obj) {
				try {
					if ((long) obj.get("ret") == 0) {
						JSONArray list = (JSONArray) obj.get("data");
						Log.i(LOG_TAG, "***** data size : " + list.size());
						for (int i = 0; i < list.size(); i++) {
							//JSONObject json = obj.getJSONArray("data")[i];
							JSONObject json = (JSONObject) list.get(i);
							//Log.i(LOG_TAG, "data " + i + " : " + json.toString());
							ParkingManager.instance().all.Add(json);
						}
						if (ParkingManager.instance().all.list.size() > 0) {
							final Handler handler = new Handler(Looper.getMainLooper());
							new Thread(new Runnable() {
								@Override
								public void run() {
									handler.post(new Runnable() {
										public void run() {
											setProgressBarIndeterminateVisibility(false);
											makePOIdataOverlay(naverMap);
											// 포천시청
											//mMapController.setMapCenter(127.200595, 37.8945858);
											// 순천시청
											//mMapController.setMapCenter(127.48718, 34.95035);
										}
									});
								}
							}).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

/*
	private void loadParkingData(LatLng center) {
		if(ParkingManager.instance().all.list.size() == 0)
			return;
		//Web.VO vo = new Web.VO(Web.URL + "/api/parkinglot.php");
		//Web.VO vo = new Web.VO(Web.URL + "/beaconparking/api/LoadParkingLot.php"); // php
		Web.VO vo = new Web.VO(Web.URL + "/parkinglot/geocode/list.json"); // jsp
		vo.AddParam("lat", my_Location.latitude + "");
		vo.AddParam("lng", my_Location.longitude + "");
		vo.AddParam("distance", "5");	// 5km
		Web.send(vo, new Web.Callback() {
			@Override
			public void callback(JSONObject obj) {
				try {
					if ((long) obj.get("ret") == 0) {
						JSONArray list = (JSONArray) obj.get("data");
						final ArrayList<ParkingManager.ParkingVO> nearList = new ArrayList<ParkingManager.ParkingVO>();
						Log.i(LOG_TAG, "data size : " + list.size());
						for (int i = 0; i < list.size(); i++) {
							//JSONObject json = obj.getJSONArray("data")[i];
							JSONObject json = (JSONObject) list.get(i);
							Log.i(LOG_TAG, "data " + i + " : " + json.toString());
							ParkingManager.ParkingVO vo = ParkingManager.instance().all.Add(json);
							nearList.add(vo);
							Log.i(LOG_TAG, "loadParking:" + vo.name + " " + vo.parked + "/" + vo.space);
						}
						if (list.size() > 0) {
							final Handler handler = new Handler(Looper.getMainLooper());
							new Thread(new Runnable() {
								@Override
								public void run() {
									handler.post(new Runnable() {
										public void run() {
											updatePOIdataOverlay(nearList);
										}
									});
								}
							}).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

*/

	private void loadParkingSearchData(String search) {

		final LatLng myLocation = my_Location;
		//Log.i("result", "***** mMapLocationManager.getMyLocation() " + myLocation);

		ParkingManager.instance().search.Clear();
		listViewSliderParking.setAdapter(null);
//		Web.VO vo = new Web.VO(Web.URL + "/api/parkinglot.php");
//		Web.VO vo = new Web.VO(Web.URL + "/beaconparking/api/LoadParkingLot.php"); // php
		Web.VO vo = new Web.VO(Web.URL + "/parkinglot/geocode/list.json"); // jsp
//		Web.VO vo = new Web.VO("http://192.168.0.8:8080/guardzone/parkinglot/geocode/list.json"); // jsp

		vo.AddParam("name", search);
		Web.send(vo, new Web.Callback() {
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void callback(JSONObject obj) {
				try {
					if ((long) obj.get("ret") == 0 && obj.get("acc").equals("ok")) {
						JSONArray list = (JSONArray) obj.get("data");
						Log.i(LOG_TAG, "loadParkingSearchData data size : " + list.size());
						for (int i = 0; i < list.size(); i++) {
							JSONObject json = (JSONObject) list.get(i);
							Log.i(LOG_TAG, "data " + i + " : " + json.toString());
							ParkingManager.instance().search.Add(json);
						}

						/* Search한 Data들의 거리를 저장 (현재위치 & 검색data) */
						for(int i = 0; i < ParkingManager.instance().search.list.size(); i++){
							ParkingManager.ParkingVO pv = ParkingManager.instance().search.list.get(i);
							LatLng to = new LatLng(pv.lng, pv.lat);
							ParkingManager.instance().search.list.get(i).distance = myLocation.distanceTo(to);
							Log.i("result", "***** data " + i + " / Name : " + ParkingManager.instance().search.list.get(i).name + "   " +
									String.format("%.2f", ParkingManager.instance().search.list.get(i).distance/1000) + " km");
						}

						if (ParkingManager.instance().search.list.size() > 0) {
							final Handler handler = new Handler(Looper.getMainLooper());
							new Thread(new Runnable() {
								@Override
								public void run() {
									handler.post(new Runnable() {
										public void run() {
											ArrayList<ParkingManager.ParkingVO> items = new ArrayList<ParkingManager.ParkingVO>();
											ArrayList<ParkingManager.ParkingVO> searchsorted = ParkingManager.instance().search.list;

											for (int i = 0; i < ParkingManager.instance().search.list.size(); i++) {
												/* Search한 Data들만 Sort. */
												Collections.sort(searchsorted, new Comparator<ParkingManager.ParkingVO>() {
													@Override
													public int compare(ParkingManager.ParkingVO var1, ParkingManager.ParkingVO var2) {
														if (var1.distance > var2.distance)
															return 1;
														if (var1.distance < var2.distance)
															return -1;
														return 0;
													}
												});
											}
											items=searchsorted;

											/* 선언한 Adapter에 Search한 items를 넘김. */
											MySimpleArrayAdapter adapterpp = new MySimpleArrayAdapter(NMapViewer.this, items);
											listViewSliderParking.setAdapter(adapterpp);
										}
									});
								}
							}).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/* Sorted Search Data를 View에 그린다. */
	public class MySimpleArrayAdapter extends ArrayAdapter<ParkingManager.ParkingVO> {
		private final Context context;
		private final ArrayList<ParkingManager.ParkingVO> values;

		public MySimpleArrayAdapter(Context context, ArrayList<ParkingManager.ParkingVO> values) {
			super(context, -1, values);
			this.context = context;
			this.values = values;
		}

		/* getView로 Sorted Search Data를 하나씩 그린다. */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_item, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.text1);
			TextView distView = (TextView) rowView.findViewById(R.id.text2);

			if(position >= 0){
				textView.setText(values.get(position).name);
				distView.setText(String.format("%.2f", (values.get(position).distance)/1000) + " km");
			}
			return rowView;
		}
	}


	private void makePOIdataOverlay(NaverMap naverMap) {  // naverMap을 Onready()로 부터 받아와야 지도에 마커표시 가능!
		InfoWindow infoWindow = new InfoWindow();
		infoWindow.setOnClickListener(overlay -> {
			infoWindow.close();
			return true;
		});
		infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(NMapViewer.this) {
			@NonNull
			@Override
			public CharSequence getText(@NonNull InfoWindow infoWindow) {
				return "정보 창 내용";
			}
		});

		ParkingManager.ParkingVO vo;
		List<Marker> markers = new ArrayList<>();

		for (int i=0; i < ParkingManager.instance().all.list.size(); i++) {
			/* get Parking Data */
			vo = ParkingManager.instance().all.list.get(i);

			Marker marker = new Marker();
			marker.setPosition(new LatLng(vo.lat, vo.lng));
			marker.setTag(vo.name);

			marker.setOnClickListener(overlay -> {
				infoWindow.open(marker);
				return true;
			});

			marker.setMap(naverMap);
			markers.add(marker);
			Log.i("*** NaverMap", "Data Load Success!! : "+ i + "번째 - " + vo.name + " 주차장     " + marker.getPosition() );
		}

//		infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(NMapViewer.this) {
//			@NonNull
//			@Override
//			public CharSequence getText(@NonNull InfoWindow infoWindow) {
//				return "정보 창 내용";
//			}
//		});


	/*

		// Markers for POI item
		int markerId = NMapPOIflagType.SPOT;

		// set POI data
		NMapPOIdata poiData = new NMapPOIdata(ParkingManager.instance().all.list.size(), mMapViewerResourceProvider);
		poiData.beginPOIdata(ParkingManager.instance().all.list.size());

		// get Parking Data
		ParkingManager.ParkingVO vo = ParkingManager.instance().all.list.get(0);
		NMapPOIitem item = poiData.addPOIitem(vo.lng, vo.lat, vo.name, markerId, 0);

		if(!vo.isEmpty()) {
			item.setMarkerId(NMapPOIflagType.DISABLE);
		}
		item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW);
		for (int i = 1; i < ParkingManager.instance().all.list.size(); i++) {
			vo = ParkingManager.instance().all.list.get(i);
			item = poiData.addPOIitem(vo.lng, vo.lat, vo.name, markerId, 0);
			if(!vo.isEmpty()) {
				item.setMarkerId(NMapPOIflagType.DISABLE);
			}
		}
		poiData.endPOIdata();
	*/
	}

/*
	private void updatePOIdataOverlay(ArrayList<ParkingManager.ParkingVO> list) {
		Log.i(LOG_TAG, "updatePOIdataOverlay");

		for (int i = 0; i < list.size(); i++) {
			ParkingManager.ParkingVO vo = list.get(i);
			int index = ParkingManager.instance().all.GetIndex(vo.id);

			if(selectedOverlayView != null && selectedOverlayView.vo.id.equals(vo.id)) {
				//Log.i(LOG_TAG, "updateOverlayView:" + vo.name + " " + vo.parked + "/" + vo.space);
				selectedOverlayView.updateParkingVO(vo);
			}
		}

		if(ParkingManager.instance().all.isChange()) {
			ParkingManager.instance().all.clearChange();
			NMapPOIdata data = poiDataOverlay.getPOIdata();
			for (int i = 0; i < data.count(); i++) {
				NMapPOIitem item = data.getPOIitem(i);
				//Log.i(LOG_TAG, "item info:" + item.toString());
			}
			while(data.count() > 0) {
				data.removePOIitem(0);
			}
			poiDataOverlay = mOverlayManager.createPOIdataOverlay(data, null);
			mOverlayManager.clearCalloutOverlayWith(poiDataOverlay);
			makePOIdataOverlay();
		}
	}

*/


	/* MyLocation Listener */
	/* NMapView.OnMapStateChangeListener onMapViewStateChangeListener 선언 다시 여기로 보내야함. */



/*
	private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

		@Override
		public boolean isLocationTracking() {
			if (mMapLocationManager != null) {
				if (mMapLocationManager.isMyLocationEnabled()) {
					return mMapLocationManager.isMyLocationFixed();
				}
			}
			return false;
		}

	};
*/


/*
	private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {
		@Override
		public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

			if (overlayItem != null) {
				// [TEST] 말풍선 오버레이를 뷰로 설정함
				//ParkingManager.ParkingVO vo = ParkingManager.instance().Get(overlayItem.getTitle());
				Log.i(LOG_TAG, overlayItem.getPoint().getLongitude() + " : " + overlayItem.getPoint().getLatitude());
				ParkingManager.ParkingVO vo = ParkingManager.instance().all.Get(overlayItem.getPoint().getLongitude(), overlayItem.getPoint().getLatitude());
				String title = overlayItem.getTitle();
				if (vo != null) {
					selectedOverlayView = new NMapCalloutCustomOverlayView(NMapViewer.this, itemOverlay, overlayItem, itemBounds, vo);
					return selectedOverlayView;
				} else {
					Log.i(LOG_TAG, "nothing parking ");
				}
			}

			// null을 반환하면 말풍선 오버레이를 표시하지 않음
			return null;
		}

	};
 */


	/* Local Functions */
	private static boolean mIsMapEnlared = true;

	private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);

	/*
		int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
		int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
		int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);

		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
		//mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);

		if (mIsMapEnlared) {
			mMapView.setScalingFactor(3.0F);
		} else {
			mMapView.setScalingFactor(1.0F);
		}
	*/

	}

/*

	private void saveInstanceState() {
		if (mPreferences == null) {
			return;
		}

		NGeoPoint center = mMapController.getMapCenter();
		int level = mMapController.getZoomLevel();
		int viewMode = mMapController.getMapViewMode();
		boolean trafficMode = mMapController.getMapViewTrafficMode();
		boolean bicycleMode = mMapController.getMapViewBicycleMode();

		SharedPreferences.Editor edit = mPreferences.edit();

		edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
		edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
		edit.putInt(KEY_ZOOM_LEVEL, level);
		edit.putInt(KEY_VIEW_MODE, viewMode);
		edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
		edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

		edit.commit();

	}

*/

	/**
	 * Invoked during init to give the Activity a chance to set up its Menu.
	 *
	 * @param menu the Menu to which entries may be added
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		//menu.findItem(R.id.action_revert).setEnabled((viewMode != NMapView.VIEW_MODE_VECTOR) || isTraffic || mOverlayManager.sizeofOverlays() > 0);
//		menu.findItem(R.id.action_vector).setChecked(viewMode == NMapView.VIEW_MODE_VECTOR);
//		menu.findItem(R.id.action_satellite).setChecked(viewMode == NMapView.VIEW_MODE_HYBRID);
//		menu.findItem(R.id.action_traffic).setChecked(isTraffic);
//		menu.findItem(R.id.action_bicycle).setChecked(isBicycle);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(LOG_TAG, "selected:" + item.getItemId() + " " + item.toString() );
		switch (item.getItemId()) {
			case android.R.id.home:
				showSearch();
				return true;
			case R.id.action_my_location:
				updateMap(my_Location);
				//startMyLocation();
				return true;

		}
		return false;
	}

	private void invalidateMenu() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			invalidateOptionsMenu();
		}
	}

	private static final long AUTO_ROTATE_INTERVAL = 2000;
	private final Handler mHnadler = new Handler();
	private final Runnable mTestAutoRotation = new Runnable() {
		@Override
		public void run() {
//        	if (mMapView.isAutoRotateEnabled()) {
//    			float degree = (float)Math.random()*360;
//
//    			degree = mMapView.getRoateAngle() + 30;
//
//    			mMapView.setRotateAngle(degree);
//
//            	mHnadler.postDelayed(mTestAutoRotation, AUTO_ROTATE_INTERVAL);
//        	}
		}
	};

	/**
	 * Container view class to rotate map view.
	 */
	public class MapContainerView extends ViewGroup {

		public MapContainerView(Context context) {
			super(context);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			final int width = getWidth();
			final int height = getHeight();
			final int count = getChildCount();

			int childWidth = 0;
			int childHeight = 0;
			int childLeft;
			int childTop;
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);
				childWidth = view.getMeasuredWidth();
				childHeight = view.getMeasuredHeight();
				childLeft = (width - childWidth) / 2;
				childTop = (height - childHeight) / 2;
				view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
			}

			if (changed) {
				//?!?!!!!!?!?!?!?!
				//mOverlayManager.onSizeChanged(width, height);
				onSizeChanged(childWidth, childHeight, width, height);
			}
		}

	/*
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
			int sizeSpecWidth = widthMeasureSpec;
			int sizeSpecHeight = heightMeasureSpec;

			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);

				if (view instanceof NMapView) {
					if (mMapView.isAutoRotateEnabled()) {
						int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
						sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
						sizeSpecHeight = sizeSpecWidth;
					}
				}

				view.measure(sizeSpecWidth, sizeSpecHeight);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	 */
	}

	public AlertDialog createDialog(String title, String message, String confirmName, String cancelName, DialogInterface.OnClickListener callbackConfirm, DialogInterface.OnClickListener callbackCancel) {
		AlertDialog.Builder ab = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
		ab.setTitle(title);
		ab.setMessage(message);
		ab.setCancelable(true);
		//ab.setIcon(getResources().getDrawable(R.drawable.ic))
		ab.setPositiveButton(confirmName, callbackConfirm);
		ab.setNegativeButton(cancelName, callbackCancel);

		return ab.create();
	}

	public void setDismiss(AlertDialog dialog) {
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
}
