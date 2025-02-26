package com.github.jvsena42.floresta_node

import android.app.Application
import com.github.jvsena42.floresta_node.data.FlorestaRpc
import com.github.jvsena42.floresta_node.domain.floresta.FlorestaRpcImpl
import com.github.jvsena42.floresta_node.presentation.ui.screens.node.NodeViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
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
    viewModel { NodeViewModel(florestaRpc = get()) }
}

val domainModule = module {
    single<FlorestaRpc> { FlorestaRpcImpl(gson = Gson()) }
}