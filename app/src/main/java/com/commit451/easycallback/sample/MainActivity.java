package com.commit451.easycallback.sample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.easycallback.EasyCallback;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    interface Google {
        @GET("about")
        Call<ResponseBody> getAbout();
    }

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.text);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.google.com/")
                .client(new OkHttpClient())
                //.addConverterFactory(LoganSquareConverterFactory.create())
                .build();
        Google google = retrofit.create(Google.class);

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
    }
}
