package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBooks(query:String) : Result<List<Book>,DataError.Remote>
    // here we use DataError is because we also need to fetch the book from local db
    suspend fun getBookDescription(bookId: String) : Result<String?,DataError>
    // we cnt use BookEntity here because it will couple with data layer
    fun getFavoritesBooks() : Flow<List<Book>>
    fun isBookFavorite(id: String): Flow<Boolean>
    suspend fun markAsFavorite(book : Book) : EmptyResult<DataError.Local>
    suspend fun deleteFromFavorites(id : String)
}


