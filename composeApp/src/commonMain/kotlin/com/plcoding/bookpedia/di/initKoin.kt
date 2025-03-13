package com.plcoding.bookpedia.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null){
    startKoin {
        config?.invoke(this)
        // By combining these modules, Koin is able to inject both common and platform-specific dependencies
            // into your shared code.
        modules(sharedModule, platformModule)
    }
}

