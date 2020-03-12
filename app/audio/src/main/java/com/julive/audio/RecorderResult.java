package com.julive.audio;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public final class RecorderResult implements Parcelable {

    private long startRecordingTime = -1;
    private File recorderFile;
    /**
     * 录音唯一Id
     */
    private String recorderId = "0";

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public long getStartRecordingTime() {
        return startRecordingTime;
    }

    public void setStartRecordingTime(long startRecordingTime) {
        this.startRecordingTime = startRecordingTime;
    }

    public File getRecorderFile() {
        return recorderFile;
    }

    public void setRecorderFile(File recorderFile) {
        this.recorderFile = recorderFile;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.startRecordingTime);
        dest.writeSerializable(this.recorderFile);
        dest.writeString(this.recorderId);
    }

    public RecorderResult() {
    }

    protected RecorderResult(Parcel in) {
        this.startRecordingTime = in.readLong();
        this.recorderFile = (File) in.readSerializable();
        this.recorderId = in.readString();
    }

    public static final Creator<RecorderResult> CREATOR = new Creator<RecorderResult>() {
        @Override
        public RecorderResult createFromParcel(Parcel source) {
            return new RecorderResult(source);
        }

        @Override
        public RecorderResult[] newArray(int size) {
            return new RecorderResult[size];
        }
    };

    @Override
    public String toString() {
        return "RecorderResult{" +
                "startRecordingTime=" + startRecordingTime +
                ", recorderFile=" + recorderFile +
                ", recorderId='" + recorderId + '\'' +
                '}';
    }
}
