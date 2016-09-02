package com.commit451.easycallback;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Test the OkHttp side of things
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class EasyOkCallbackTest {

    static OkHttpClient client;

    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;
        client = OkHttpFactory.create();
    }

    @Test
    public void testSuccessCallback() throws Exception {
        Request request = new Request.Builder()
                .url("https://www.google.com")
                .build();

        client.newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
            }

            @Override
            public void failure(Throwable t) {
                Assert.fail(t.getMessage());
            }
        });
    }

    @Test
    public void testFailure() throws Exception {
        Request request = new Request.Builder()
                .url("https://www.google.com/404")
                .build();

        client.newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                Assert.fail("call should have failed");
            }

            @Override
            public void failure(Throwable t) {
            }
        });
    }

    @Test
    public void testParsingFailure() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/teams")
                .build();

        client.newCall(request).enqueue(new EasyOkCallback() {
            @Override
            public void success(@NonNull Response response) {
                Assert.fail("This test should have failed");
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(t instanceof HttpException);
                String json = OkHttpUtil.toString(((HttpException)t).errorBody());
                Gson gson = new Gson();
                GitHubErrorBody errorBody = gson.fromJson(json, GitHubErrorBody.class);
                Assert.assertEquals("Not Found", errorBody.message);
            }
        });
    }
}
