package com.codecamp.bitfit.util;

/**
 * Created by MaxBreida on 13.02.18.
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Simple timer class which count up until stopped.
 * Inspired by {@link android.os.CountDownTimer}
 */
public class CountUpTimer {

    private final long interval;
    private long base;
    private TextView textView;

    public CountUpTimer(long interval, TextView textView) {
        this.interval = interval;
        this.textView = textView;
    }

    public void start() {
        base = SystemClock.elapsedRealtime();
        handler.sendMessage(handler.obtainMessage(MSG));
    }

    public void stop() {
        handler.removeMessages(MSG);
    }

    public void reset() {
        synchronized (this) {
            base = SystemClock.elapsedRealtime();
        }
    }

    public void onTick(long elapsedTime){
        textView.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(elapsedTime),
                TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime)))
        );
    }

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