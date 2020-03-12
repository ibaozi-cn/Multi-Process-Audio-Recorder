package com.julive.audio.impl

import android.media.MediaRecorder
import com.julive.audio.IRecorder
import com.julive.audio.RecorderConfig
import com.julive.audio.common.RecorderOutFormat
import com.julive.audio.common.RecorderState


class JLMediaRecorder : IRecorder {

    private var mMediaRecorder: MediaRecorder? = null
    private var mState = RecorderState.IDLE

    @Synchronized
    override fun startRecording(recorderConfig: RecorderConfig): String {
        try {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder?.setAudioSource(recorderConfig.audioSource)

            when (recorderConfig.recorderOutFormat) {
                RecorderOutFormat.MPEG_4 -> {
                    mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                }
                RecorderOutFormat.AMR_WB -> {
                    mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
                    mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
                }
                else -> {
                    mMediaRecorder?.reset()
                    mMediaRecorder?.release()
                    mMediaRecorder = null
                    return "MediaRecorder 不支持 AudioFormat.PCM"
                }
            }
        } catch (e: IllegalStateException) {
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            mMediaRecorder = null
            return "Error initializing media recorder 初始化失败";
        }
        return try {
            val file = recorderConfig.recorderFile
            file.parentFile.mkdirs()
            file.createNewFile()
            val outputPath: String = file.absolutePath

            mMediaRecorder?.setOutputFile(outputPath)
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            mState = RecorderState.RECORDING
            ""
        } catch (e: Exception) {
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            mMediaRecorder = null
            recorderConfig.recorderFile.delete()
            e.toString()
        }
    }

    override fun isRecording(): Boolean {
        return mState == RecorderState.RECORDING
    }

    @Synchronized
    override fun stopRecording() {
        try {
            if (mState == RecorderState.RECORDING) {
                mMediaRecorder?.stop()
                mMediaRecorder?.reset()
                mMediaRecorder?.release()
            }
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
        }
        mMediaRecorder = null
        mState = RecorderState.IDLE
    }

    override fun state(): RecorderState {
        return mState
    }

}