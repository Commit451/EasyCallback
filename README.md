# EasyCallback
Easier Retrofit and OkHttp callbacks

[![Build Status](https://travis-ci.org/Commit451/EasyCallback.svg?branch=master)](https://travis-ci.org/Commit451/EasyCallback)
[![](https://jitpack.io/v/Commit451/EasyCallback.svg)](https://jitpack.io/#Commit451/EasyCallback)

Many times when using Retrofit or OkHttp, you would probably want your `!isSuccessful()` responses to just fall through into your failure case. That is what this does for you. It is very reminiscent of Retrofit 1.X callback days.

# Usage
Usage is simple and similar to regular Retrofit Callbacks:
```java
api.getUsers().enqueue(new EasyCallback<ResponseBody>() {
    @Override
    public void success(@NonNull ResponseBody response) {
        textView.setText("Things worked!");
    }

    @Override
    public void failure(Throwable t) {
        mTextView.setText("Something went wrong");
    }
});
```   

If you actually need to perform different actions depending on the HTTP error status code, you can still do so:
```java
@Override
public void failure(Throwable t) {
    Timber.e(t, null);
    if (t instanceof HttpException) {
        switch (((HttpException) t).response().code()) {
            case 404:
                mTextView.setText("Thingy not found");
                break;
            case 500:
                mTextView.setText("Our server broke :(");
                break;
        }
    } else {
        mTextView.setText("Some generic error message");
    }
}
```
You can also still retrieve information about the call with things like `call()` or `response()` if needed.

`EasyOkCallback` is an OkHttp specific flavor of the Callback which also checks `isSuccessful()` and will also by default post the result on the main thread for simplicity.

# Note
If your API happens to return a `200` code, but contains an empty body, this will fall through to the `failure` block due to the fact that we check for `null` in the `onResponse` and redirect to `failure` if the response is null. You can work around this by calling `allowNullBodies(true)` on the callback.

License
--------

    Copyright 2016 Commit 451

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
