package com.wm4n.lockscreen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class LockScreenService extends Service {

    public final static String KEY_START_LOCKED = "start_locked";

    private static LockScreenService mInstance;
    private GestureDetector mGestureDetector;
    private ScreenReceiver mScreenReceiver;
    private WindowManager mWindowManager;
    private ViewGroup mRootLayout;
    private View mUnlockHintView;
    private int mOverlayIndex = 0;

    private final static int[] BACKGROUND = {
            R.drawable.overlay_option1,
            R.drawable.overlay_option2,
            R.drawable.overlay_option3,
            R.drawable.overlay_option4,
            R.drawable.overlay_option5
    };

    private class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent && null != intent.getAction()) {
                final String ACTION = intent.getAction();
                if(Intent.ACTION_SCREEN_OFF.equals(ACTION)) {
                    Intent lockIntent = new Intent(context, LockScreenActivity.class);
                    lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lockIntent);
                    showOverlay();
                }
                else if(Intent.ACTION_SCREEN_ON.equals(ACTION)){
                    // do nothing
                }
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            hideOverlay();
            return super.onDoubleTap(e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        mGestureDetector = new GestureDetector(this, new GestureListener());
        mScreenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, intentFilter);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootLayout = (ViewGroup) layoutInflater.inflate(R.layout.service_lock_screen, null, false);
        mUnlockHintView = mRootLayout.findViewById(R.id.unlock_text);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, // SYSTEM_ERROR helps hideOverlay status bar (notification)
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 0;
        layoutParams.y = 0;

        mRootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return true;
            }
        });

        mWindowManager.addView(mRootLayout, layoutParams);// add the view
    }

    @Override
    public void onDestroy() {
        mWindowManager.removeView(mRootLayout);
        unregisterReceiver(mScreenReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startLocked = false;
        if(null != intent) {
            startLocked = intent.getBooleanExtra(KEY_START_LOCKED, false);
        }
        if(startLocked) {
            Intent lockIntent = new Intent(this, LockScreenActivity.class);
            lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(lockIntent);

            showOverlay();
        }
        return START_STICKY;
    }

    public void showOverlay() {
        if(null == mRootLayout) return;
        mRootLayout.setVisibility(View.VISIBLE);
        mUnlockHintView.setBackgroundResource(BACKGROUND[mOverlayIndex]);
        mOverlayIndex ++;
        if(mOverlayIndex >= BACKGROUND.length) {
            mOverlayIndex = 0;
        }
    }

    public void hideOverlay() {
        if(null == mRootLayout) return;
        mRootLayout.setVisibility(View.GONE);
        sendBroadcast(new Intent("com.wm4n.lockscreen.FINISH"));
    }

    public static LockScreenService getInstance() {
        return mInstance;
    }
}
