package com.julive.recorder

import android.Manifest.permission
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.julive.audio.RecorderConfig
import com.julive.audio.RecorderManager
import com.julive.audio.common.RecorderOutFormat
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class MainActivity : AppCompatActivity() ,EasyPermissions.PermissionCallbacks{

    val STORAGE = permission.WRITE_EXTERNAL_STORAGE
    val RECORD_AUDIO = permission.RECORD_AUDIO

    var sRequiredPermissions = arrayOf(
        STORAGE, RECORD_AUDIO
    )

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
            Log.d("MainActivity", error + " ${result.toString()}")
            textState.text = error
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RecorderManager.initialize(this) {
            if (it) {
                textState.text = "连接服务成功"
                RecorderManager.registerCallback(defaultRecorderCallBack)
            } else {
                textState.text = "连接服务失败"
                Log.d("MainActivity", "服务连接失败")
            }
        }
        setOnClick()
        hasPermission()
    }

    private fun setOnClick() {
        buttonStart.setOnClickListener {
            hasPermission()
            recorderConfig =
                RecorderConfig(File(filePath.plus("CallRecordings").plus(File.separator) + System.currentTimeMillis() + ".pcm"))
            recorderConfig?.weight = 199
            recorderConfig?.recorderOutFormat = RecorderOutFormat.PCM
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

    @AfterPermissionGranted(100)
    private fun hasPermission() {
        if (!EasyPermissions.hasPermissions(
                this,
                *sRequiredPermissions
            )
        ) {
            EasyPermissions.requestPermissions(
                this,
                "请求权限",
                100, *sRequiredPermissions
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(
                this,
                perms
            )
        ) { //这里需要重新设置Rationale和title，否则默认是英文格式
            AppSettingsDialog.Builder(this)
                .setRationale("没有该权限，此应用程序可能无法正常工作。打开应用设置屏幕以修改应用权限")
                .setTitle("必需权限")
                .build()
                .show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}
