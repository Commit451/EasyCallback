package com.commit451.easycallback;

import android.support.annotation.NonNull;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Test the OkHttp side of things
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = NetworkSecurityPolicyWorkaround.class)
public class EasyOkCallbackTest {

    static OkHttpClient client;

    private static OkHttpClient client() {
        if (client == null) {
            client = OkHttpFactory.create();
        }
        return client;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;
    }

    @Test
    public void testSuccessCallback() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        Request request = new Request.Builder()
                .url("https://www.google.com")
                .build();

        client().newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                Assert.fail(t.getMessage());
            }
        }.callbackOnMainThread(false));
        countDownLatch.await();
    }

    @Test
    public void testFailureCallback() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        Request request = new Request.Builder()
                .url("https://www.google.com/404")
                .build();

        client().newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                countDownLatch.countDown();
                Assert.fail("This test should have failed");
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
            }
        }.callbackOnMainThread(false));
        countDownLatch.await();
    }
}
