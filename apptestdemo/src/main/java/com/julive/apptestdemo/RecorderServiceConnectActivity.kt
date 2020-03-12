package com.julive.apptestdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.julive.audio.RecorderConfig
import com.julive.audio.RecorderManager
import com.julive.audio.common.RecorderOutFormat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class RecorderServiceConnectActivity : AppCompatActivity() {

    private val filePath =
        Environment.getExternalStorageDirectory().absolutePath.plus(File.separator)

    private var recorderConfig: RecorderConfig? = null

    private val defaultRecorderCallBack = RecorderManager.DefaultRecorderCallBack(
        {
            Log.d("MainActivity", it.toString())
            textState.text = "录音中"
        }, {
            Log.d("MainActivity", it.toString())
            textState.text = "已停止"
        }, { error, result ->
            Log.d("MainActivity", error + " ${result.toString()} currentThread: ${Thread.currentThread().name}")
            textState.text = error
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RecorderManager.initialize(this) {
            if(it){
                textState.text = "连接服务成功"
                RecorderManager.registerCallback(defaultRecorderCallBack)
            }else{
                textState.text = "连接服务失败"
                Log.d("MainActivity","服务连接失败")
            }
        }
        setOnClick()
    }

    private fun setOnClick() {
        buttonStart.setOnClickListener {
            recorderConfig = RecorderConfig(File(filePath.plus("CallRecordings").plus(File.separator) + System.currentTimeMillis() + ".amr"))
            recorderConfig?.weight = 100
            recorderConfig?.recorderOutFormat = RecorderOutFormat.MPEG_4
            RecorderManager.startRecording(recorderConfig)
        }
        buttonStop.setOnClickListener {
            RecorderManager.stopRecording(recorderConfig)
        }
    }

    override fun onStart() {
        super.onStart()
        RecorderManager.registerCallback(defaultRecorderCallBack)
    }

    override fun onDestroy() {
        RecorderManager.unInitialize()
        super.onDestroy()
    }
}
