package com.nhn.android.mapviewer;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innocns.innoparking.R;

/**
 * Created by Administrator on 2017-01-06..
 */
public class PopupAddressActivity extends Activity {

    private TextView mTextAddress;
    private TextView mTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.)
        setContentView(R.layout.popup_address);
        findViewById(R.id.popup_address_btn_ok).setOnClickListener(popupClickOkListener);

        Intent intent = getIntent();
        mTextName = (TextView)findViewById(R.id.popup_address_name_text);
        mTextName.setText(intent.getExtras().getString("name"));

        mTextAddress = (TextView)findViewById(R.id.popup_address_text);
        mTextAddress.setText(intent.getExtras().getString("address"));

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getApplicationContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            final android.content.ClipData clipData = android.content.ClipData
                    .newPlainText("text label", intent.getExtras().getString("address"));
            clipboardManager.setPrimaryClip(clipData);
        } else {
            final android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getApplicationContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(intent.getExtras().getString("address"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private final View.OnClickListener popupClickOkListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (popupClickOkListener != null) {
                finish();
            }
        }
    };
}
