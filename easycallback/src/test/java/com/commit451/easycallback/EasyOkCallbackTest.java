package com.commit451.easycallback;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

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
        final FailureHolder failureHolder = new FailureHolder();
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
                failureHolder.failure = true;
                failureHolder.message = t.getMessage();
            }
        }.callbackOnMainThread(false));
        countDownLatch.await();
        if (failureHolder.failure) {
            Assert.fail(failureHolder.message);
        }
    }

    @Test
    public void testFailure() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        final FailureHolder failureHolder = new FailureHolder();
        Request request = new Request.Builder()
                .url("https://www.google.com/404")
                .build();

        client().newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                countDownLatch.countDown();
                failureHolder.failure = true;
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
            }
        }.callbackOnMainThread(false));
        countDownLatch.await();

        if (failureHolder.failure) {
            Assert.fail("This test should have failed");
        }
    }

    @Test
    public void testParsingFailure() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        Request request = new Request.Builder()
                .url("https://api.github.com/teams")
                .build();
        final FailureHolder throwableHolder = new FailureHolder();

        client().newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                countDownLatch.countDown();
                Assert.fail("This test should have failed");
            }

            @Override
            public void failure(Throwable t) {
                throwableHolder.throwable = t;
                countDownLatch.countDown();

            }
        }.callbackOnMainThread(false));
        countDownLatch.await();
        Throwable t = throwableHolder.throwable;
        Assert.assertTrue(t instanceof HttpException);
        String json = OkUtil.toString(((HttpException)t).errorBody());
        Gson gson = new Gson();
        GitHubErrorBody errorBody = gson.fromJson(json, GitHubErrorBody.class);
        Assert.assertEquals("Not Found", errorBody.message);
    }
}
