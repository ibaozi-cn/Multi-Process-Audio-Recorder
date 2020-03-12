package com.julive.audio.impl

import android.media.AudioFormat
import android.media.AudioRecord
import com.julive.audio.IRecorder
import com.julive.audio.RecorderConfig
import com.julive.audio.common.RecorderState
import com.julive.audio.thread.SingleThreadPool
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class JLAudioRecorder : IRecorder {

    //采样率
    private val SAMPLING_RATE = 16000
    private val CHANNEL_IN: Int = AudioFormat.CHANNEL_IN_STEREO
    //比特率
    private val FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE =
        AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_OUT_STEREO, FORMAT)

    private var mState = RecorderState.IDLE
    private var mAudioRecord: AudioRecord? = null

    override fun startRecording(recorderConfig: RecorderConfig): String {
        try {
            mAudioRecord = AudioRecord(
                recorderConfig.audioSource,
                SAMPLING_RATE,
                CHANNEL_IN,
                FORMAT,
                BUFFER_SIZE
            )
        } catch (e: IllegalStateException) {
            return "Error initializing media recorder 初始化失败"
        }
        return try {
            val file = recorderConfig.recorderFile
            file.parentFile.mkdirs()
            file.createNewFile()
            val outputPath: String = file.absolutePath
            mAudioRecord?.startRecording()
            mState = RecorderState.RECORDING
            startRecordingThread(file)
            ""
        } catch (e: Exception) {
            recorderConfig.recorderFile.delete()
            e.toString()
        }
    }

    private fun startRecordingThread(file: File) {
        SingleThreadPool.execute(RecordingRunnable(file))
    }

    override fun isRecording(): Boolean {
        return mState == RecorderState.RECORDING
    }

    override fun stopRecording() {
        if (mAudioRecord != null) {
            if (state() == RecorderState.RECORDING) {
                mAudioRecord?.stop()
                mAudioRecord?.release()
            }
            mAudioRecord = null
            mState = RecorderState.IDLE
        }
    }

    override fun state(): RecorderState {
        return mState
    }

    inner class RecordingRunnable(val file: File) : Runnable {
        override fun run() {
            val buffer: ByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE)
            try {
                FileOutputStream(file).use { outStream ->
                    while (state() == RecorderState.RECORDING) {
                        val result: Int = mAudioRecord?.read(buffer, BUFFER_SIZE) ?: 0
                        if (result < 0) {
                            throw RuntimeException(
                                "Reading of audio buffer failed: " +
                                        getBufferReadFailureReason(result)
                            )
                        }
                        outStream.write(buffer.array(), 0, BUFFER_SIZE)
                        buffer.clear()
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException("Writing of recorded audio failed", e)
            }
        }
    }

    private fun getBufferReadFailureReason(errorCode: Int): String? {
        return when (errorCode) {
            AudioRecord.ERROR_INVALID_OPERATION -> "ERROR_INVALID_OPERATION"
            AudioRecord.ERROR_BAD_VALUE -> "ERROR_BAD_VALUE"
            AudioRecord.ERROR_DEAD_OBJECT -> "ERROR_DEAD_OBJECT"
            AudioRecord.ERROR -> "ERROR"
            else -> "Unknown ($errorCode)"
        }
    }

}