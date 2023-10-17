package com.example.moviebookingalarm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    companion object {
        var application: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}