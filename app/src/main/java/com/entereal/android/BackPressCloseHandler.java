package com.entereal.android;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-02-13.
 */
public class BackPressCloseHandler {

    //backKeyPressedTime은 백버튼이 눌린 마지막 시간을 기록
    private long backKeyPressedTime = 0;
    private long MaxTime = 3000;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    /* onBackPressed() 메서드는 현재 시간이 마지막 백버튼 누른 시간으로부터
       2초 이상 지났으면, 마지막 백버튼 눌린 시간을 현재 시간으로 갱신하고 showGuide()를 실행한다.
       2초 이상 지나지 않았으면, Activity를 종료한다.
       참고로, 2초는 Toast.LENGTH_SHORT의 기본 값이다.
       showGuide() 메서드는 Toast를 이용해서 메시지를 출력한다.
    */
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + MaxTime) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + MaxTime) {
            toast.cancel();
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT); toast.show();

        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(15);
    }
}
