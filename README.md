# Firefox Accounts for Android

[![Build Status](https://travis-ci.org/crankycoder/fxa-android.png)](https://travis-ci.org/crankycoder/fxa-android)

# Building a debug version from command line #

The build system is smart enough to automatically download and install
all the parts of the Android SDK for you.  If you cannot build, you
can either try to fix your Android dev enviroment to fit the
android/build.gradle requirements - or you can simply remove
ANDROID_HOME, and all traces of your Android SDK from your PATH.

```
./gradlew clean build
```


This code is originally based off of

https://github.com/wareninja/generic-oauth2-login-for-android/

but has been substantially modified for use with Firefox Accounts.



## Using the Firefox Accounts for Android library.

This library is available on JCenter now.

Assuming you're using gradle, all you need to do is include a the fxa dependency into your build.gradle file and you're good to go.

```
dependencies {
    compile 'org.mozilla.accounts.fxa:fxa:0.9.12'
}
```

The code in this project is structured as a full Android application and all the Firefox accoutns library source code is available under the libraries/fxa subdirectory.

Under `android/build.gradle`, you will find a fully working build.gradle configuration for a working Android application with directives to build either from the library source, or to use the JCenter hosted precompiled AAR file.

For all release tags in github, we will set the build.gradle file to use official JCenter releases.



## Uploading to Bintray

Uploads of the fxa library into the bintray repository is handled with
an API key.  Current documentation for obtaining an API key can be
found on the [bintray usermanual site](https://bintray.com/docs/usermanual/).


A sample bintray.properties file has been included in this project
which will allow you to enter your API key and user name.  The
template is in `bintray.properties.template`.  Just rename it to
bintray.properties and update the user and apikey to your own bintray
credentials.



