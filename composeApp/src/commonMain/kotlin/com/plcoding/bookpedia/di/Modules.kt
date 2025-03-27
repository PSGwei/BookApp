package com.plcoding.bookpedia.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.plcoding.bookpedia.book.data.database.DatabaseFactory
import com.plcoding.bookpedia.book.data.database.FavoriteBookDatabase
import com.plcoding.bookpedia.book.data.network.KtorRemoteBookDataSource
import com.plcoding.bookpedia.book.data.network.RemoteBookDataSource
import com.plcoding.bookpedia.book.data.repository.DefaultBookRepository
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailViewModel
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

// // each platform needs Engine, but each implementation is different, so need to apply this
expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()
    // get<>(): Koin requesting another dependency
    // because DatabaseFactory is defined using expect/actual
    // Koin will resolve this to the correct actual platform-specific implementation of DatabaseFactory
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())   //  making Room work on non-Android platforms
            // takes all the configuration provided, then constructs the actual instance of your
            .build()
    }
    single { get<FavoriteBookDatabase>().favoriteBookDao}  // obtain from the previous single{}
    viewModelOf(::BookListViewModel)
    viewModelOf(::SelectedBookViewModel)
    viewModelOf(::BookDetailViewModel)
}


