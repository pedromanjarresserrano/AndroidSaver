package com.service.saver.saverservice.util

import android.os.AsyncTask
import java.lang.Exception

class Util {

    companion object {
        class Task(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                try {
                    handler()
                } catch (e: Exception) {
                    println(e.stackTrace)
                }
                return null
            }
        }

    }
}