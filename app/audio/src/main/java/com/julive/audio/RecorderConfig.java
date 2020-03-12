package com.julive.audio;

import android.media.MediaRecorder;
import android.os.Parcel;
import android.os.Parcelable;

import com.julive.audio.common.RecorderOutFormat;

import java.io.File;

public final class RecorderConfig implements Parcelable {
    /**
     * 录音唯一Id
     */
    private String recorderId;
    /**
     * 录音文件
     */
    private File recorderFile;
    /**
     * 音源
     * int VOICE_CALL = 4
     * int MIC = 1
     * int DEFAULT = 0
     */
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    /**
     * 录音格式
     */
    private RecorderOutFormat recorderOutFormat = RecorderOutFormat.MPEG_4;
    /**
     * 1~100
     */
    private int weight = 1;

    public RecorderConfig(File recorderFile) {
        this.recorderFile = recorderFile;
        this.recorderId = recorderFile.getAbsolutePath();
    }

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public File getRecorderFile() {
        return recorderFile;
    }

    public void setRecorderFile(File recorderFile) {
        this.recorderFile = recorderFile;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public RecorderOutFormat getRecorderOutFormat() {
        return recorderOutFormat;
    }

    public void setRecorderOutFormat(RecorderOutFormat recorderOutFormat) {
        this.recorderOutFormat = recorderOutFormat;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "RecorderConfig{" +
                "recorderId='" + recorderId + '\'' +
                ", recorderFile=" + recorderFile +
                ", audioSource=" + audioSource +
                ", audioFormat=" + recorderOutFormat +
                ", weight=" + weight +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recorderId);
        dest.writeSerializable(this.recorderFile);
        dest.writeInt(this.audioSource);
        dest.writeInt(this.recorderOutFormat == null ? -1 : this.recorderOutFormat.ordinal());
        dest.writeInt(this.weight);
    }

    public RecorderConfig() {
    }

    protected RecorderConfig(Parcel in) {
        this.recorderId = in.readString();
        this.recorderFile = (File) in.readSerializable();
        this.audioSource = in.readInt();
        int tmpAudioFormat = in.readInt();
        this.recorderOutFormat = tmpAudioFormat == -1 ? null : RecorderOutFormat.values()[tmpAudioFormat];
        this.weight = in.readInt();
    }

    public static final Parcelable.Creator<RecorderConfig> CREATOR = new Parcelable.Creator<RecorderConfig>() {
        @Override
        public RecorderConfig createFromParcel(Parcel source) {
            return new RecorderConfig(source);
        }

        @Override
        public RecorderConfig[] newArray(int size) {
            return new RecorderConfig[size];
        }
    };
}
