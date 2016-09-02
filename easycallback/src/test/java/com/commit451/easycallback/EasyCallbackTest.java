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

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Since we use an immediate callback executor, despite the calls
 * looking async, they are actually happening synchronously
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class EasyCallbackTest {

    static Google google;
    static GitHub gitHub;

    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;
    }

    /**
     * Defer this from {@link #setUp()} due to issues with Robolectric Executor service not existing yet
     */
    public static void init() {
        if (google == null) {
            google = RetrofitFactory.create("https://google.com", Google.class);
            gitHub = RetrofitFactory.create("https://api.github.com", GitHub.class);
        }
    }

    @Test
    public void testSuccessCallback() throws Exception {
        init();
        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.assertNotNull(call());
                Assert.assertTrue(response().isSuccessful());
                Assert.fail();
            }

            @Override
            public void failure(Throwable t) {
                Assert.fail("This should have been success");
            }
        });
    }

    @Test
    public void testVoidCallback() throws Exception {
        init();
        google.getVoid().enqueue(new EasyCallback<Void>() {
            @Override
            public void success(@NonNull Void response) {
            }

            @Override
            public void failure(Throwable t) {
                Assert.fail("call should have been successful");
            }
        }.allowNullBodies(true));
    }

    @Test
    public void testFailureCallback() throws Exception {
        init();
        google.get404Error().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.fail("This call should have failed");
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertNotNull(t);
            }
        });
    }

    @Test
    public void test404ParsingBody() throws Exception {
        init();
        gitHub.get404().enqueue(new EasyFailureCallback<Void>() {
            @Override
            public void failure(Throwable t) {
                Assert.assertNotNull(t);
                Assert.assertTrue(t instanceof HttpException);
                int code = ((HttpException) t).response().code();
                Assert.assertEquals(404, code);
                String json = OkHttpUtil.toString(((HttpException) t).errorBody());
                Gson gson = new Gson();
                GitHubErrorBody body = gson.fromJson(json, GitHubErrorBody.class);
                Assert.assertEquals("Not Found", body.message);
            }
        }.allowNullBodies(true));
    }

    @Test
    public void testParsing() throws Exception {
        init();
        gitHub.contributors("square", "retrofit").enqueue(new EasyCallback<List<Contributor>>() {
            @Override
            public void success(@NonNull List<Contributor> response) {
                Contributor contributor = response.get(0);
                Assert.assertNotNull(contributor.login);
            }

            @Override
            public void failure(Throwable t) {
                Assert.fail("call should have been success");
            }
        });
    }
}