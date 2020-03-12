package com.julive.audio;

import com.julive.audio.common.RecorderState;

public interface IRecorder {

    String startRecording(RecorderConfig recorderConfig);

    void stopRecording();

    RecorderState state();

    boolean isRecording();

}
