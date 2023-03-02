package com.nikhil.here.youtube_poc

import android.app.Application
import com.mocklets.pluto.Pluto
import com.mocklets.pluto.PlutoLog
import com.mocklets.pluto.modules.exceptions.ANRException
import com.mocklets.pluto.modules.exceptions.ANRListener
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        initializePluto()
    }
    private fun initializePluto() {
        Pluto.initialize(applicationContext)
        Pluto.setANRListener(object: ANRListener {
            override fun onAppNotResponding(exception: ANRException) {
                exception.printStackTrace()
                PlutoLog.e("ANR", exception.threadStateMap)
            }
        })
    }
}