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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = NetworkSecurityPolicyWorkaround.class)
public class CallbackTest {

    interface Google {
        @GET("about")
        Call<ResponseBody> getAbout();

        @GET("home")
        Call<Void> getVoid();

        @GET("404")
        Call<ResponseBody> get404Error();
    }

    static Google google;

    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://google.com")
                .client(new OkHttpClient())
                .build();
        google = restAdapter.create(Google.class);
    }

    @Test
    public void testSuccessCallback() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                Assert.fail("Call should have been a success");
            }
        });
        countDownLatch.await();
    }

    @Test
    public void testVoidCallback() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.getVoid().enqueue(new EasyCallback<Void>() {
            @Override
            public void success(@NonNull Void response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                Assert.fail("Call should have been a success");
            }
        }.allowNullBodies(true));
        countDownLatch.await();
    }

    @Test
    public void testFailureCallback() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.get404Error().enqueue(new EasyFailureCallback<ResponseBody>() {
            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(t instanceof HttpException);
                int code = ((HttpException)t).getCode();
                Assert.assertEquals(404, code);
            }
        });
        countDownLatch.await();
    }
}