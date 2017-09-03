package com.example.woong.instamaterialtest

/**
 * Created by woong on 2017. 8. 29..
 */

import android.app.Application;

import timber.log.Timber;

class InstaMaterialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}