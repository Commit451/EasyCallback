# EasyCallback
Easier Retrofit callbacks

[![Build Status](https://travis-ci.org/Commit451/EasyCallback.svg?branch=master)](https://travis-ci.org/Commit451/EasyCallback)
[![](https://jitpack.io/v/Commit451/EasyCallback.svg)](https://jitpack.io/#Commit451/EasyCallback)

Many times when using Retrofit, you would probably want your `isSuccessful()` responses to just fall through into your failure case. That is what this do. It is very reminiscent of Retrofit 1.X callback days

# Usage
Usage is simple:
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
