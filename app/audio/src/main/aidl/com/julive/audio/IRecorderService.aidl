// IRecorderService.aidl
package com.julive.audio;

import com.julive.audio.RecorderConfig;
import com.julive.audio.RecorderResult;
import com.julive.audio.IRecorderCallBack;

// Declare any non-default types here with import statements

interface IRecorderService {

    void startRecording(in RecorderConfig recorderConfig);

    void stopRecording(in RecorderConfig recorderConfig);

    boolean isRecording(in RecorderConfig recorderConfig);

    RecorderResult getActiveRecording();

    void registerCallback(IRecorderCallBack callBack);

    void unregisterCallback(IRecorderCallBack callBack);

}
