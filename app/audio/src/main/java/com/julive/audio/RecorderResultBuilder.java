package com.julive.audio;

import java.io.File;

final class RecorderResultBuilder {
    private long startRecordingTime = -1;
    private File recorderFile;
    private String recorderId = "0";

    private RecorderResultBuilder() {
    }

    public static RecorderResultBuilder aRecorderResult() {
        return new RecorderResultBuilder();
    }

    public RecorderResultBuilder withStartRecordingTime(long startRecordingTime) {
        this.startRecordingTime = startRecordingTime;
        return this;
    }

    public RecorderResultBuilder withRecorderFile(File recorderFile) {
        this.recorderFile = recorderFile;
        return this;
    }

    public RecorderResultBuilder withRecorderId(String recorderId) {
        this.recorderId = recorderId;
        return this;
    }

    public RecorderResult build() {
        RecorderResult recorderResult = new RecorderResult();
        recorderResult.setStartRecordingTime(startRecordingTime);
        recorderResult.setRecorderFile(recorderFile);
        recorderResult.setRecorderId(recorderId);
        return recorderResult;
    }
}
