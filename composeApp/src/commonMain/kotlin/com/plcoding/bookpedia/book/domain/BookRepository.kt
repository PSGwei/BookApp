package com.plcoding.bookpedia.book.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result

interface BookRepository {
    suspend fun searchBooks(query:String) : Result<List<Book>,DataError.Remote>
    // here we use DataError is because we also need to fetch the book from local db
    suspend fun getBookDescription(bookId: String) : Result<String?,DataError>
}


