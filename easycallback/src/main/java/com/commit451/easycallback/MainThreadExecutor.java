package com.commit451.easycallback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Main thread executor, copied from {@link retrofit2.Retrofit}. Pass this to OkHttp if you want
 * callbacks on the main thread using {@link EasyOkCallback#executor(Executor)}
 */
public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override public void execute(@NonNull Runnable r) {
        handler.post(r);
    }
}