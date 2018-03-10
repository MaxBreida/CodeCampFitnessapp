package com.codecamp.bitfit.util;

/**
 * Created by MaxBreida on 13.02.18.
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

/**
 * Simple timer class which count up until stopped.
 * Inspired by {@link android.os.CountDownTimer}
 */
public abstract class CountUpTimer {

    private final long interval;
    private long base;
    private long stopTime;
    private TextView textView;

    public CountUpTimer(long interval) {
        this.interval = interval;
    }

    public CountUpTimer(long interval, TextView textView) {
        this.interval = interval;
        this.textView = textView;
    }

    public void start() {
        base = SystemClock.elapsedRealtime();
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void resume(){
        base = SystemClock.elapsedRealtime() - (stopTime - base);
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void stop(){
        stopTime = SystemClock.elapsedRealtime();
        handler.removeMessages(MSG);
    }

    public void reset() {
        synchronized (this) {
            base = SystemClock.elapsedRealtime();
        }
    }

    public abstract void onTick(long elapsedTime);
//    public void onTick(long elapsedTime){
//        textView.setText(Util.getMillisAsTimeString(elapsedTime));
//    }

    private static final int MSG = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountUpTimer.this) {
                long elapsedTime = SystemClock.elapsedRealtime() - base;
                onTick(elapsedTime);
                sendMessageDelayed(obtainMessage(MSG), interval);
            }
        }
    };
}
