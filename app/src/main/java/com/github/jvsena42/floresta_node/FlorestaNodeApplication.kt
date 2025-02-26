package com.github.jvsena42.floresta_node

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class FlorestaNodeApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@FlorestaNodeApplication)
            modules(
                presentationModule,
                domainModule
            )
        }
    }
}

val presentationModule = module {

}

val domainModule = module {

}