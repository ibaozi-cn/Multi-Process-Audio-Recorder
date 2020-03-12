package com.julive.audio

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.IBinder
import android.os.RemoteCallbackList
import androidx.core.content.ContextCompat
import com.julive.audio.common.RecorderOutFormat
import com.julive.audio.common.logD
import com.julive.audio.impl.JLAudioRecorder
import com.julive.audio.impl.JLMediaRecorder

class RecorderService : Service() {

    private var iRecorder: IRecorder? = null
    private var currentRecorderResult: RecorderResult = RecorderResult()
    private var currentWeight: Int = -1

    private val remoteCallbackList: RemoteCallbackList<IRecorderCallBack> = RemoteCallbackList()

    private val mBinder: IRecorderService.Stub = object : IRecorderService.Stub() {

        override fun startRecording(recorderConfig: RecorderConfig) {
            startRecordingInternal(recorderConfig)
        }

        override fun stopRecording(recorderConfig: RecorderConfig) {
            if (recorderConfig.recorderId == currentRecorderResult.recorderId)
                stopRecordingInternal()
            else {
                notifyCallBack {
                    it.onException(
                        "Cannot stop the current recording because the recorderId is not the same as the current recording",
                        currentRecorderResult
                    )
                }
            }
        }

        override fun getActiveRecording(): RecorderResult? {
            return currentRecorderResult
        }

        override fun isRecording(recorderConfig: RecorderConfig?): Boolean {
            return if (recorderConfig?.recorderId == currentRecorderResult.recorderId)
                iRecorder?.isRecording ?: false
            else false
        }

        override fun registerCallback(callBack: IRecorderCallBack) {
            remoteCallbackList.register(callBack)
        }

        override fun unregisterCallback(callBack: IRecorderCallBack) {
            remoteCallbackList.unregister(callBack)
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }


    @Synchronized
    private fun startRecordingInternal(recorderConfig: RecorderConfig) {

        val willStartRecorderResult =
            RecorderResultBuilder.aRecorderResult().withRecorderFile(recorderConfig.recorderFile)
                .withRecorderId(recorderConfig.recorderId).build()

        if (ContextCompat.checkSelfPermission(
                this@RecorderService,
                android.Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            logD("Record audio permission not granted, can't record")
            notifyCallBack {
                it.onException(
                    "Record audio permission not granted, can't record",
                    willStartRecorderResult
                )
            }
            return
        }

        if (ContextCompat.checkSelfPermission(
                this@RecorderService,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            logD("External storage permission not granted, can't save recorded")
            notifyCallBack {
                it.onException(
                    "External storage permission not granted, can't save recorded",
                    willStartRecorderResult
                )
            }
            return
        }

        if (isRecording()) {

            val weight = recorderConfig.weight

            if (weight < currentWeight) {
                logD("Recording with weight greater than in recording")
                notifyCallBack {
                    it.onException(
                        "Recording with weight greater than in recording",
                        willStartRecorderResult
                    )
                }
                return
            }

            if (weight > currentWeight) {
                //只要权重大于当前权重，立即停止当前。
                stopRecordingInternal()
            }

            if (weight == currentWeight) {
                if (recorderConfig.recorderId == currentRecorderResult.recorderId) {
                    notifyCallBack {
                        it.onException(
                            "The same recording cannot be started repeatedly",
                            willStartRecorderResult
                        )
                    }
                    return
                } else {
                    stopRecordingInternal()
                }
            }

            startRecorder(recorderConfig, willStartRecorderResult)

        } else {

            startRecorder(recorderConfig, willStartRecorderResult)

        }

    }

    private fun startRecorder(
        recorderConfig: RecorderConfig,
        willStartRecorderResult: RecorderResult
    ) {
        logD("startRecording result ${willStartRecorderResult.toString()}")

        iRecorder = when (recorderConfig.recorderOutFormat) {
            RecorderOutFormat.MPEG_4, RecorderOutFormat.AMR_WB -> {
                JLMediaRecorder()
            }
            RecorderOutFormat.PCM -> {
                JLAudioRecorder()
            }
        }

        val result = iRecorder?.startRecording(recorderConfig)

        if (!result.isNullOrEmpty()) {
            logD("startRecording result $result")
            notifyCallBack {
                it.onException(result, willStartRecorderResult)
            }
        } else {
            currentWeight = recorderConfig.weight
            notifyCallBack {
                it.onStart(willStartRecorderResult)
            }
            currentRecorderResult = willStartRecorderResult
        }
    }

    private fun isRecording(): Boolean {
        return iRecorder?.isRecording ?: false
    }

    @Synchronized
    private fun stopRecordingInternal() {
        logD("stopRecordingInternal")
        iRecorder?.stopRecording()
        currentWeight = -1
        iRecorder = null
        MediaScannerConnection.scanFile(
            this,
            arrayOf(currentRecorderResult.recorderFile?.absolutePath),
            null,
            null
        )
        notifyCallBack {
            it.onStop(currentRecorderResult)
        }
    }

    private fun notifyCallBack(done: (IRecorderCallBack) -> Unit) {
        val size = remoteCallbackList.beginBroadcast()
        logD("recorded notifyCallBack  size $size")
        (0 until size).forEach {
            done(remoteCallbackList.getBroadcastItem(it))
        }
        remoteCallbackList.finishBroadcast()
    }

}


