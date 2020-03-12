package com.julive.audio.thread

import android.os.Handler
import android.os.Looper
import java.lang.Exception

val MAIN: Handler by lazy {
    Handler(Looper.getMainLooper())
}

fun postMainThread(runnable: ()->Unit) {
    try {
        MAIN.post(runnable)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}