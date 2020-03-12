package com.julive.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import com.julive.audio.common.logE
import com.julive.audio.thread.postMainThread

object RecorderManager {

    private var mRecorderService: IRecorderService? = null
    private var isServiceConnected = false
    private var mRecorderCallBack: IRecorderCallBack? = null
    private var mServiceConnectState: ((Boolean) -> Unit)? = null
    private var mApplicationContext: Context? = null

    /**
     * Binder可能会意外死忙（比如Service Crash），Client监听到Binder死忙后可以进行重连服务等操作
     */
    private val deathRecipient: DeathRecipient = object : DeathRecipient {
        override fun binderDied() {
            if (mRecorderService != null) {
                mRecorderService?.asBinder()?.unlinkToDeath(this, 0)
                mRecorderService = null
            }
            //重连
            val serviceIntent = Intent()
            serviceIntent.`package` = "com.julive.recorder"
            serviceIntent.action = "com.julive.audio.service"
            mApplicationContext?.bindService(
                serviceIntent,
                mConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }


    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mRecorderService = IRecorderService.Stub.asInterface(service)
            mRecorderService?.asBinder()?.linkToDeath(deathRecipient, 0)
            isServiceConnected = true
            mServiceConnectState?.invoke(true)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isServiceConnected = false
            mRecorderService = null
            logE("onServiceDisconnected:name=$name")
        }
    }


    fun registerCallback(defaultRecorderCallBack: DefaultRecorderCallBack) {
        if (isServiceConnected) {
            mRecorderCallBack = defaultRecorderCallBack
            mRecorderService?.registerCallback(defaultRecorderCallBack)
        }
    }

    fun unregisterCallback() {
        mRecorderService?.unregisterCallback(mRecorderCallBack)
    }

    fun initialize(context: Context?, serviceConnectState: ((Boolean) -> Unit)? = null) {
        mApplicationContext = context?.applicationContext
        if (!isServiceConnected) {
            this.mServiceConnectState = serviceConnectState
            val serviceIntent = Intent()
            serviceIntent.`package` = "com.julive.recorder"
            serviceIntent.action = "com.julive.audio.service"
            val isCanBind = mApplicationContext?.bindService(
                serviceIntent,
                mConnection,
                Context.BIND_AUTO_CREATE
            ) ?: false
            if (!isCanBind) {
                logE("isCanBind:$isCanBind")
                this.mServiceConnectState?.invoke(false)
                bindSelfService()
            }
        }
    }

    fun unInitialize() {
        if (isServiceConnected) {
            unregisterCallback()
            mApplicationContext?.unbindService(mConnection)
            isServiceConnected = false
        }
    }

    private fun bindSelfService() {
        val serviceIntent = Intent(mApplicationContext, RecorderService::class.java)
        val isSelfBind =
            mApplicationContext?.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
        logE("isSelfBind:$isSelfBind")
    }

    private fun isServiceConnected() = isServiceConnected

    fun startRecording(recorderConfig: RecorderConfig?) {
        if (recorderConfig != null)
            mRecorderService?.startRecording(recorderConfig)
    }

    fun stopRecording(recorderConfig: RecorderConfig?) {
        if (recorderConfig != null)
            mRecorderService?.stopRecording(recorderConfig)
    }

    fun isRecording(recorderConfig: RecorderConfig?): Boolean {
        return mRecorderService?.isRecording(recorderConfig) ?: false
    }

    class DefaultRecorderCallBack(
        val onStartFun: (RecorderResult) -> Unit,
        val onStopFun: (RecorderResult) -> Unit,
        val onExceptionFun: (String, RecorderResult) -> Unit
    ) : IRecorderCallBack.Stub() {
        override fun onStop(result: RecorderResult) {
            postMainThread {
                onStopFun(result)
            }
        }

        override fun onException(error: String, result: RecorderResult) {
            postMainThread {
                onExceptionFun(error, result)
            }
        }

        override fun onStart(result: RecorderResult) {
            postMainThread {
                onStartFun(result)
            }
        }
    }

}