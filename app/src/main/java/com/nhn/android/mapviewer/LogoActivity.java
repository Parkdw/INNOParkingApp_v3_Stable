package com.nhn.android.mapviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.innocns.innoparking.R;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.mapviewer.NMapViewer;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.Timer;
import java.util.TimerTask;



import android.provider.Settings;
import android.location.LocationManager;
import android.content.DialogInterface;
import android.widget.Toast;


public class LogoActivity extends Activity {
    // TimerTask란 Timer가 스케쥴을 관리하기 위해 사용하는 "작업"
    private TimerTask mTask;
    // 스케쥴을 관리 해주는 클래스
    private Timer mTimer;


    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private NMapLocationManager mMapLocationManager;
    // 안드로이드 ViewGroup 클래스를 상속받은 클래스로서 지도 데이터를 화면에 표시
    private NMapView mMapView;
    // 지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속
    private NMapMyLocationOverlay mMyLocationOverlay;
    // 단말기의 나침반 기능을 사용하기 위한 클래스입니다.
    private NMapCompassManager mMapCompassManager;
    private NMapViewer.MapContainerView mMapContainerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo);

        //Log.d("TAG", "시작?!?!?!?!?!?!?!?!?!?!?!??!?!?!?!?!?!?!?!?! ?????");

        /* LocationManager로 현재 GPS정보에 접근. */
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        /* GPS - ON or OFF 체크. */
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Log.d("GPS", "result : GPS OFF ?????");
            GPSAlert();
        }
        else {
            //Log.d("GPS", "result : GPS ON !!!!!");

            /* 위치 권한 여부 확인. */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LogoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
            /* 위치 권한 허용 */
            else{
                mTask = new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), NMapViewer.class);
                        startActivity(intent);
                    }
                };

                // Timer 생성
                mTimer = new Timer();
                // TimerTask를 delay 시간 만큼 후에 실행
                mTimer.schedule(mTask, 1800);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void GPSAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyCustomDialogStyle);
        builder.setTitle("GPS 사용 여부 설정");
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        builder.setMessage("GPS를 사용하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /* 사용자가 GPS를 ON 선택!! */
                        //Toast.makeText(getApplicationContext(),"예를 선택했습니다.",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        /* 설정 화면에서 어떤 설정을 하고 onActivityResult를 통해 앱으로 돌아오기 위해 requestcode를 걸어줌. */
                        startActivityForResult(intent, 0);

                        return;
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getApplicationContext(),"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();

                        /* 사용자가 GPS OFF 선택, 위치권한 물어볼 필요 없이 기본 위치로 이동. */
                        Intent intent = new Intent(getApplicationContext(), NMapViewer.class);
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                Intent intent = new Intent(getApplicationContext(), NMapViewer.class);
                startActivity(intent);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("result", "result : " + resultCode);
//        Log.d("result", "앱으로 다시 돌아옴!!");

        switch(requestCode) {
            case 0 :
                //Log.d("result", "시작화면 기다리기!!!!");

                /* 안드로이드 setting에서 GPS를 ON한 후, activity_logo화면에서 대기. */
                    mTask = new TimerTask() {
                @Override
                public void run() {
                    //Log.d("result", "1.8초 후 !!!");

                    /* GPS가 ON된 후, 위치권한 체크. */
                    if (ActivityCompat.checkSelfPermission(LogoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("result", "위치 권한 물어본다!!!!!!!!!!!!!");
                        ActivityCompat.requestPermissions(LogoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                    /* 위치권한은 허용, GPS를 다시 ON 했을 때, (GPS가 OFF인 상황에서) */
                    else{
                        Intent intent = new Intent(getApplicationContext(), NMapViewer.class);
                        startActivity(intent);
                    }
                }
            };

                // Timer 생성
                mTimer = new Timer();
                // TimerTask를 delay 시간 만큼 후에 실행
                mTimer.schedule(mTask, 1800);

                break;
        }
        return;
    }

    // 어플리케이션 종료 시에는 Timer의 작업을 취소
    @Override
    protected void onDestroy() {
        if(mTimer!=null)
            mTimer.cancel();
        super.onDestroy();
    }

    //Activity Context를 감싸준다
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    public void doPermissionGrantedStuffs() {
        Boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
        // 현재 위치 탐색 중인지 여부를 반환한다.
        if (isMyLocationEnabled) {
            //Log.d("성공","성공 : " + isMyLocationEnabled);
            stopMyLocation();
            mMapView.postInvalidate();
        }
        else{	//현재 위치를 탐색 중이 아니면
            //Log.d("현재위치 실패","현재위치 실패");
        }
    }
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
}
