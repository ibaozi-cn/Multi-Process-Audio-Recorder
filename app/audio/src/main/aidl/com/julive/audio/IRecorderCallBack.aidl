// IRecorderCallBack.aidl
package com.julive.audio;

// Declare any non-default types here with import statements
import com.julive.audio.RecorderResult;

interface IRecorderCallBack {

    void onStart(in RecorderResult result);

    void onStop(in RecorderResult result);

    void onException(String error,in RecorderResult result);

}

