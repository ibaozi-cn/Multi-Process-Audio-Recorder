package com.julive.audio.common

import android.util.Log
import com.julive.audio.BuildConfig

private const val TAG = "RecorderService"
private val DBG = BuildConfig.DEBUG

fun logD(content: String) {
    if (DBG)
        Log.d(TAG, content)
}

fun logW(content: String) {
    if (DBG)
        Log.w(TAG, content)
}

fun logE(content: String) {
    if (DBG)
        Log.e(TAG, content)
}
