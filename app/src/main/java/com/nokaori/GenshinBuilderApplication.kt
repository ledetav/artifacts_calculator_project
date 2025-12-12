package com.nokaori.genshinaibuilder

import android.app.Application
import com.nokaori.genshinaibuilder.di.AppContainer
import com.nokaori.genshinaibuilder.di.DefaultAppContainer

class GenshinBuilderApplication : Application() {

    // Ссылка на контейнер, доступная всему приложению
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Инициализируем контейнер при старте приложения
        container = DefaultAppContainer(this)
    }
}