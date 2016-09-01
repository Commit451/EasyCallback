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
 * failure blocks, the test will still pass. So, we have to use {@link TestHolder} to keep
 * up with the status of the test
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
        final TestHolder testHolder = new TestHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                testHolder.call = call();
                testHolder.retrofitResponse = response();
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                testHolder.failure = true;
                testHolder.throwable = t;
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (testHolder.failure) {
            Assert.fail(testHolder.throwable.getMessage());
        } else{
            Assert.assertNotNull(testHolder.call);
            Assert.assertTrue(testHolder.retrofitResponse.isSuccessful());
        }
    }

    @Test
    public void testVoidCallback() throws Exception {
        init();
        final TestHolder testHolder = new TestHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.getVoid().enqueue(new EasyCallback<Void>() {
            @Override
            public void success(@NonNull Void response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                testHolder.failure = true;
                testHolder.throwable = t;
            }
        }.allowNullBodies(true));
        countDownLatch.await();
        if (testHolder.failure) {
            Assert.fail(testHolder.throwable.getMessage());
        }
    }

    @Test
    public void testFailureCallback() throws Exception {
        init();
        final TestHolder testHolder = new TestHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.get404Error().enqueue(new EasyFailureCallback<ResponseBody>() {
            @Override
            public void failure(Throwable t) {
                testHolder.failure = true;
                testHolder.throwable = t;
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (!testHolder.failure) {
            Assert.fail("This call should have failed");
        } else {
            Throwable e = testHolder.throwable;
            Assert.assertTrue(e instanceof HttpException);
            Assert.assertEquals(404, ((HttpException)e).response().code());
        }
    }

    @Test
    public void test404ParsingBody() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        final TestHolder testHolder = new TestHolder();
        gitHub.get404().enqueue(new EasyFailureCallback<Void>() {
            @Override
            public void failure(Throwable t) {
                testHolder.throwable = t;
                countDownLatch.countDown();
            }
        }.allowNullBodies(true));

        countDownLatch.await();
        Throwable t = testHolder.throwable;
        Assert.assertNotNull(t);
        Assert.assertTrue(t instanceof HttpException);
        int code = ((HttpException) t).response().code();
        Assert.assertEquals(404, code);
        String json = OkUtil.toString(((HttpException) t).errorBody());
        Gson gson = new Gson();
        GitHubErrorBody body = gson.fromJson(json, GitHubErrorBody.class);
        Assert.assertEquals("Not Found", body.message);
    }

    @Test
    public void testParsing() throws Exception {
        init();
        final TestHolder testHolder = new TestHolder();
        final SuccessHolder<List<Contributor>> successHolder = new SuccessHolder<>();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        gitHub.contributors("square", "retrofit").enqueue(new EasyCallback<List<Contributor>>() {
            @Override
            public void success(@NonNull List<Contributor> response) {
                successHolder.setValue(response);
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                testHolder.failure = true;
                testHolder.throwable = t;
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (testHolder.failure) {
            Assert.fail(testHolder.throwable.getMessage());
        } else {
            List<Contributor> response = successHolder.getValue();
            Contributor contributor = response.get(0);
            Assert.assertNotNull(contributor.login);
        }
    }
}