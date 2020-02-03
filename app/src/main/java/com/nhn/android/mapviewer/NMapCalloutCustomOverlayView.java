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
package com.nhn.android.mapviewer;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.entereal.android.ParkingManager;
import com.innocns.innoparking.R;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.skt.Tmap.TMapTapi;

import java.util.ArrayList;
import java.util.List;

public class NMapCalloutCustomOverlayView extends NMapCalloutOverlayView {
	private static final String LOG_TAG = "NMapViewer";
	private View mCalloutView;
	private TextView mCalloutText;
	private View mRightArrow;
	private TextView mCalloutTextPrice;
	private TextView mCalloutTextUseAble;
	public ParkingManager.ParkingVO vo = null;
	private ImageView mCalloutTextPriceBox;
	private ImageButton mCalloutButtonAddress;
	android.support.v7.app.AlertDialog adialog;

	Context thisContext = null;

	public NMapCalloutCustomOverlayView(Context context, NMapOverlay itemOverlay, NMapOverlayItem item, Rect itemBounds, ParkingManager.ParkingVO vo) {
		super(context, itemOverlay, item, itemBounds);

		thisContext = context;

		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		li.inflate(R.layout.callout_overlay_view, this, true);

		mCalloutView = findViewById(R.id.callout_overlay);
		mCalloutText = (TextView) mCalloutView.findViewById(R.id.callout_text);
		//mRightArrow = findViewById(R.id.callout_rightArrow);
		//mCalloutTextPrice = (TextView) mCalloutView.findViewById(R.id.callout_price);
		mCalloutTextUseAble = (TextView) mCalloutView.findViewById(R.id.callout_useable);
		mCalloutButtonAddress = (ImageButton) mCalloutView.findViewById(R.id.callout_btn_address);

		mCalloutView.setOnClickListener(callOutClickListener);
		findViewById(R.id.callout_btn_address).setOnClickListener(callOutClickAddressListener);
		findViewById(R.id.callout_btn_tmap).setOnClickListener(callOutClickTMapListener);
		findViewById(R.id.callout_btn_kakao).setOnClickListener(callOutClickKakaoListener);

		updateParkingVO(vo);

		if (item instanceof NMapPOIitem) {
			if (((NMapPOIitem) item).hasRightAccessory() == false) {
				//mRightArrow.setVisibility(View.GONE);
			}
		}

		/* 이용요금 기능 삭제  2020.01.30 */
//		mCalloutTextPriceBox = (ImageView) mCalloutView.findViewById(R.id.callout_layout_price_box);
//
//		final ViewTreeObserver obs = mCalloutView.getViewTreeObserver();
//		obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//
//			@Override
//			public boolean onPreDraw() {
//				int width = mCalloutView.getWidth();
//				int height = mCalloutView.getHeight();
//
//				mCalloutTextPriceBox.getLayoutParams().width = width;
//				mCalloutTextPriceBox.requestLayout();
//
//				//1번만 동작하도록
//				ViewTreeObserver obs = mCalloutView.getViewTreeObserver();
//				obs.removeOnPreDrawListener(this);
//
//				return false;
//			}
//		});
	}

	public void updateParkingVO(ParkingManager.ParkingVO vo) {
		this.vo = vo;

		//mCalloutTextPrice.setText(vo.desc); // 이용요금 기능 삭제 2020.01.30
		mCalloutText.setText(vo.name);
		if(vo.isEmpty()) {
			mCalloutTextUseAble.setTextColor(0xfffffc00);
			mCalloutTextUseAble.setText("주차 가능(" + (vo.space - vo.parked) + "/" + vo.space + ")");
		} else {
			mCalloutTextUseAble.setTextColor(0xffff0000);
			mCalloutTextUseAble.setText("주차 불가능(" + (vo.space - vo.parked) + "/" + vo.space + ")");
		}
	}

	private final OnClickListener callOutClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (mOnClickListener != null) {
				mOnClickListener.onClick(null, mItemOverlay, mOverlayItem);
			}
		}
	};

	private final OnClickListener callOutClickAddressListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (callOutClickAddressListener != null) {
				Log.i(LOG_TAG, "address click");
				Intent intent = new Intent(NMapViewer.instance.getApplicationContext(), PopupAddressActivity.class);
				intent.putExtra("name", vo.name);
				intent.putExtra("address", vo.address);
				NMapViewer.instance.startActivity(intent);
			}
		}
	};

	private final OnClickListener callOutClickKakaoListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (callOutClickKakaoListener != null) {
				Log.i(LOG_TAG, "kakao click");
				// com.kakao.kakaonavi

				Intent intent = NMapViewer.instance.getPackageManager().getLaunchIntentForPackage("com.locnall.KimGiSa");
				if(intent == null) {
					adialog = NMapViewer.instance.createDialog("알림", "카카오내비 설치페이지로\n이동 하시겠습니까?", "이동", "취소", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							NMapViewer.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.locnall.KimGiSa")));
							NMapViewer.instance.setDismiss(adialog);
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							NMapViewer.instance.setDismiss(adialog);
						}
					});

					adialog.show();
					TextView textView = (TextView) adialog.findViewById(android.R.id.message);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
				} else {
					// Location.Builder를 사용하여 Location 객체를 만든다.
					Location destination = Location.newBuilder(vo.name, vo.lng, vo.lat).build();
					NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();
					KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
					KakaoNaviService.shareDestination(NMapViewer.instance, builder.build());
				}
			}
		}
	};

	// 2020.01.29 AlertDialog 추가, T-map 경로 분할.
	private final OnClickListener callOutClickTMapListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			TextView textView = new TextView(NMapCalloutCustomOverlayView.this.getContext());
			textView.setText("Select T-map");
			textView.setPadding(20, 30, 20, 30);
			textView.setTextSize(20F);
			textView.setBackgroundColor(Color.BLACK);
			textView.setTextColor(Color.WHITE);

			final ArrayList<String> arr = new ArrayList<String>();
			arr.add("T-map (Play스토어)");
			arr.add("T-map (원스토어)" );

			AlertDialog.Builder builder = new AlertDialog.Builder(NMapCalloutCustomOverlayView.this.getContext());
			builder.setCustomTitle(textView);

			builder.setItems(arr.toArray(new String[arr.size()]), new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int pos)
				{
					//Toast.makeText(thisContext, arr.get(pos),Toast.LENGTH_LONG).show();

					// playstore T-map : com.skt.tmap.ku
					if(pos==0){
						Intent intent = NMapViewer.instance.getPackageManager().getLaunchIntentForPackage("com.skt.tmap.ku");
						if (intent == null) {
							adialog = NMapViewer.instance.createDialog("알림", "T맵 설치페이지로\n이동 하시겠습니까?", "이동", "취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NMapViewer.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.skt.tmap.ku")));
						 			NMapViewer.instance.setDismiss(adialog);
								}
							}, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NMapViewer.instance.setDismiss(adialog);
								}
							});

							adialog.show();
							TextView textView = (TextView) adialog.findViewById(android.R.id.message);
							textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
						}else {
							//Log.i(LOG_TAG, "tmap open api");
							thisContext.startActivity(intent);
						}
					}
					// onestore T-map : com.skt.skaf.l001mtm091
					else{
						Intent intent = NMapViewer.instance.getPackageManager().getLaunchIntentForPackage("com.skt.skaf.l001mtm091");
						if (intent == null) {
							adialog = NMapViewer.instance.createDialog("알림", "T맵 설치페이지로\n이동 하시겠습니까?", "이동", "취소", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NMapViewer.instance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("onestore://common/product/0000163382")));
									NMapViewer.instance.setDismiss(adialog);
								}
							}, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									NMapViewer.instance.setDismiss(adialog);
								}
							});

							adialog.show();
							TextView textView = (TextView) adialog.findViewById(android.R.id.message);
							textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 27);
						}else {
							//Log.i(LOG_TAG, "tmap open api");
							thisContext.startActivity(intent);
						}
					}
				}
			});

			android.app.AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}

	};

}
