package com.nokaori.genshinaibuilder

import android.app.Application
import com.nokaori.genshinaibuilder.di.AppContainer
import com.nokaori.genshinaibuilder.di.DefaultAppContainer

class GenshinBuilderApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}