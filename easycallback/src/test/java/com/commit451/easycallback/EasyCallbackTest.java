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
 * failure blocks, the test will still pass. So, we have to use {@link FailureHolder} to keep
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
        final FailureHolder failureHolder = new FailureHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                failureHolder.failure = true;
                failureHolder.throwable = t;
            }
        });
        countDownLatch.await();
        if (failureHolder.failure) {
            Assert.fail(failureHolder.throwable.getMessage());
        }
    }

    @Test
    public void testVoidCallback() throws Exception {
        init();
        final FailureHolder failureHolder = new FailureHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.getVoid().enqueue(new EasyCallback<Void>() {
            @Override
            public void success(@NonNull Void response) {
                countDownLatch.countDown();
            }

            @Override
            public void failure(Throwable t) {
                countDownLatch.countDown();
                failureHolder.failure = true;
                failureHolder.throwable = t;
            }
        }.allowNullBodies(true));
        countDownLatch.await();
        if (failureHolder.failure) {
            Assert.fail(failureHolder.throwable.getMessage());
        }
    }

    @Test
    public void testFailureCallback() throws Exception {
        init();
        final FailureHolder failureHolder = new FailureHolder();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        google.get404Error().enqueue(new EasyFailureCallback<ResponseBody>() {
            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(t instanceof HttpException);
                int code = ((HttpException) t).response().code();
                Assert.assertEquals(404, code);
                if (code != 404) {
                    failureHolder.failure = true;
                    failureHolder.throwable = new IllegalStateException("Code is not 404");
                }
            }
        });
        countDownLatch.await();
        if (failureHolder.failure) {
            Assert.fail(failureHolder.throwable.getMessage());
        }
    }

    @Test
    public void test404ParsingBody() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(0);
        final FailureHolder failureHolder = new FailureHolder();
        gitHub.get404().enqueue(new EasyFailureCallback<Void>() {
            @Override
            public void failure(Throwable t) {
                failureHolder.throwable = t;
                countDownLatch.countDown();

            }
        }.allowNullBodies(true));

        countDownLatch.await();
        Throwable t = failureHolder.throwable;
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
        final FailureHolder failureHolder = new FailureHolder();
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
                failureHolder.failure = true;
                failureHolder.throwable = t;
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (failureHolder.failure) {
            Assert.fail(failureHolder.throwable.getMessage());
        } else {
            List<Contributor> response = successHolder.getValue();
            Contributor contributor = response.get(0);
            Assert.assertNotNull(contributor.login);
        }
    }
}