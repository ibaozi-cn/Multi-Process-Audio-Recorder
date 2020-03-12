package com.julive.audio;

import android.media.MediaRecorder;

import com.julive.audio.common.RecorderOutFormat;

import java.io.File;

public final class RecorderConfigBuilder {
    private String recorderId;
    private File recorderFile;
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private RecorderOutFormat audioFormat = RecorderOutFormat.MPEG_4;
    private int weight = 1;

    private RecorderConfigBuilder() {
    }

    public static RecorderConfigBuilder aRecorderConfig() {
        return new RecorderConfigBuilder();
    }

    public RecorderConfigBuilder withRecorderId(String recorderId) {
        this.recorderId = recorderId;
        return this;
    }

    public RecorderConfigBuilder withRecorderFile(File recorderFile) {
        this.recorderFile = recorderFile;
        return this;
    }

    public RecorderConfigBuilder withAudioSource(int audioSource) {
        this.audioSource = audioSource;
        return this;
    }

    public RecorderConfigBuilder withAudioFormat(RecorderOutFormat audioFormat) {
        this.audioFormat = audioFormat;
        return this;
    }

    public RecorderConfigBuilder withWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public RecorderConfig build() {
        RecorderConfig recorderConfig = new RecorderConfig();
        recorderConfig.setRecorderId(recorderId);
        recorderConfig.setRecorderFile(recorderFile);
        recorderConfig.setAudioSource(audioSource);
        recorderConfig.setRecorderOutFormat(audioFormat);
        recorderConfig.setWeight(weight);
        return recorderConfig;
    }
}
