package vone.meihuayishu.com.touchassist;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainService extends Service {
    private static final String TAG = "MSL MainService";

    private ConstraintLayout toucherLayout;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private int statusBarHeight;
    private RulerView rulerView;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createToucher();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置效果为背景透明.
        params.format = PixelFormat.RGBA_8888;

        //设置窗口初始停靠位置.
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        //设置悬浮窗口长宽数据.
        params.width = 200;
        params.height = 500;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (ConstraintLayout) inflater.inflate(R.layout.main_service, null);
        toucherLayout.setFocusableInTouchMode(true);
        //添加toucherlayout
        windowManager.addView(toucherLayout, params);

        //主动计算出当前View的宽高信息.
        toucherLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        //用于检测状态栏高度.
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "状态栏高度为:" + statusBarHeight);
        rulerView = toucherLayout.findViewById(R.id.ruler);
        rulerView.setClickable(true);

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch: " + event.getAction());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_OUTSIDE:
                        rulerView.start();
                        break;
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return gestureDetector.onTouchEvent(event);
            }
        };
        rulerView.setOnTouchListener(gestureListener);
        toucherLayout.setOnTouchListener(gestureListener);
        rulerView.setOnRulerTouchListener(new RulerView.OnRulerTouchListener() {
            @Override
            public void onTouch() {
                stopSelf();
            }
        });

    }

    @Override
    public void onDestroy() {
        if (rulerView != null)
            windowManager.removeView(toucherLayout);
        super.onDestroy();
    }
}
