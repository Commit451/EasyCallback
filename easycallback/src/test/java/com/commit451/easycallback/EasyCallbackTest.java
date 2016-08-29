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

import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;

/**
 * Note about these tests: if an exception is thrown when the callback is in its success or
 * failure blocks, the test will still pass. Keep this in mind while testing
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, shadows = NetworkSecurityPolicyWorkaround.class)
public class EasyCallbackTest {

    static Google google;
    static GitHub gitHub;

    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;
    }

    public static void init() {
        if (google == null) {
            google = RetrofitFactory.create("https://google.com", Google.class);
            gitHub = RetrofitFactory.create("https://api.github.com", GitHub.class);
        }
    }

    @Test
    public void testSuccessCallback() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                Assert.fail(t.getMessage());
            }
        });
        countDownLatch.await();
    }

    @Test
    public void testVoidCallback() throws Exception {
        init();
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
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.get404Error().enqueue(new EasyFailureCallback<ResponseBody>() {
            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(t instanceof HttpException);
                int code = ((HttpException) t).response().code();
                Assert.assertEquals(404, code);
            }
        });
        countDownLatch.await();
    }

    @Test
    public void test404ParsingBody() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        gitHub.get404().enqueue(new EasyFailureCallback<Void>() {
            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(t instanceof HttpException);
                int code = ((HttpException) t).response().code();
                Assert.assertEquals(404, code);
                String json = OkUtil.toString(((HttpException) t).errorBody());
                Gson gson = new Gson();
                GitHubErrorBody body = gson.fromJson(json, GitHubErrorBody.class);
                Assert.assertEquals("Not Found", body.message);
            }
        }.allowNullBodies(true));
        countDownLatch.await();
    }

    @Test
    public void testParsing() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        gitHub.contributors("square", "retrofit").enqueue(new EasyCallback<List<Contributor>>() {
            @Override
            public void success(@NonNull List<Contributor> response) {
                Contributor contributor = response.get(0);
                Assert.assertNotNull(contributor.login);
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                Assert.fail(t.getMessage());
            }
        });
        countDownLatch.await();
    }
}