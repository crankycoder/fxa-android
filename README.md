Firefox Accounts for Android
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

