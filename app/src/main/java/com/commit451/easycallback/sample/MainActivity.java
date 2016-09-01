package com.commit451.easycallback.sample;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.easycallback.EasyCallback;
import com.commit451.easycallback.EasyOkCallback;
import com.commit451.easycallback.HttpException;
import com.commit451.easycallback.RetrofitUtil;

import junit.framework.Assert;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    interface Google {
        @GET("about")
        Call<ResponseBody> getAbout();

        @GET("home")
        Call<Void> getVoid();
    }

    TextView mTextView;
    OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.google.com/")
                .client(mOkHttpClient)
                .build();
        final Google google = retrofit.create(Google.class);

        //This will force callbacks to occur on the background OkHttp thread instead of the main thread
        Executor executor = mOkHttpClient.dispatcher().executorService();
        Retrofit backgroundCallbackRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.google.com/")
                .callbackExecutor(executor)
                .build();
        final Google backgroundGoogle = backgroundCallbackRetrofit.create(Google.class);

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                mTextView.setText("Things worked!");
            }

            @Override
            public void failure(Throwable t) {
                mTextView.setText("Something went wrong");
                Toast.makeText(MainActivity.this, "Oh noooooo", Toast.LENGTH_SHORT).show();
            }
        });

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                //This will be on a background thread now, so we will be able to execute calls synchronously
                try {
                    retrofit2.Response<ResponseBody> responseBodyResponse = google.getAbout().execute();
                    Assert.assertTrue(responseBodyResponse.isSuccessful());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(Throwable t) {
                mTextView.setText("Something went wrong");
                Toast.makeText(MainActivity.this, "Oh noooooo", Toast.LENGTH_SHORT).show();
            }
        }.executor(mOkHttpClient.dispatcher().executorService()));

        backgroundGoogle.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }
        });

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }
        }.executor(mOkHttpClient.dispatcher().executorService()));

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertTrue(Looper.myLooper() == Looper.getMainLooper());
            }
        }.successExecutor(mOkHttpClient.dispatcher().executorService()));

        google.getAbout().enqueue(new EasyCallback<ResponseBody>() {
            @Override
            public void success(@NonNull ResponseBody response) {
                Assert.assertTrue(Looper.myLooper() == Looper.getMainLooper());
            }

            @Override
            public void failure(Throwable t) {
                Assert.assertFalse(Looper.myLooper() == Looper.getMainLooper());
            }
        }.failureExecutor(mOkHttpClient.dispatcher().executorService()));

        //Showing this for demonstrational purposes. Normally you would just call
        //final Executor retrofitCallbackExecutor = retrofit.callbackExecutor();
        final Executor mainThreadExecutor = RetrofitUtil.createDefaultCallbackExecutor();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request.Builder()
                        .url("https://www.google.com")
                        .build();

                mOkHttpClient.newCall(request).enqueue(new EasyOkCallback() {
                    @Override
                    public void success(@NonNull Response response) {
                        Toast.makeText(MainActivity.this, "OkHttp Success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(Throwable t) {
                        Toast.makeText(MainActivity.this, "OkHttp error!", Toast.LENGTH_SHORT).show();
                    }
                }.allowNullBodies(true).executor(mainThreadExecutor));
            }
        });

        findViewById(R.id.button_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will 404... I hope
                Request request = new Request.Builder()
                        .url("https://github.com/asdlfkjalksdjflkajsdf")
                        .build();

                mOkHttpClient.newCall(request).enqueue(new EasyOkCallback() {
                    @Override
                    public void success(@NonNull Response response) {
                        Toast.makeText(MainActivity.this, "Success... that doesn't make sense....", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(Throwable t) {
                        if (t instanceof HttpException) {
                            Toast.makeText(MainActivity.this, "OkHttp error! Error code " + ((HttpException) t).response().code(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Not an Http error, so that is weird", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        findViewById(R.id.button_void).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                google.getVoid().enqueue(new EasyCallback<Void>() {
                    @Override
                    public void success(@NonNull Void response) {
                        Toast.makeText(MainActivity.this, "Void works when we allow null bodies!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(MainActivity.this, "Void failed...", Toast.LENGTH_SHORT).show();
                    }
                }.allowNullBodies(true));
            }
        });

    }
}
