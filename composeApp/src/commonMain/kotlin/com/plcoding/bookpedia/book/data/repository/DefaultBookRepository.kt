package com.plcoding.bookpedia.book.data.repository

import androidx.sqlite.SQLiteException
import com.plcoding.bookpedia.book.data.database.FavoriteBookDao
import com.plcoding.bookpedia.book.data.mappers.toBook
import com.plcoding.bookpedia.book.data.mappers.toBookEntity
import com.plcoding.bookpedia.book.data.network.RemoteBookDataSource
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val favoriteBookDao: FavoriteBookDao
) : BookRepository {
    // map the data model to domain model
    override suspend fun searchBooks(query: String): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource
            .searchBooks(query)
            .map { dto ->
                dto.result.map {
                    it.toBook()
                }
            }
    }

    override suspend fun getBookDescription(bookId: String): Result<String?, DataError> {
        val localResult = favoriteBookDao.getFavoriteBook(bookId)
        return if(localResult == null){
            remoteBookDataSource
                .getBookDescription(bookId)
                .map { it.description }
        }else{
            Result.Success(localResult.description)
        }
    }

    // Flow<List<Book>> will automatically emit a new List<BookEntity> whenever the data in the BookEntity table changes in the database
    override fun getFavoritesBooks(): Flow<List<Book>> {
        return favoriteBookDao
            .getFavoriteBooks()
            // outer map -> flow operator
            // executed every time the Flow emits a new list of BookEntity objects
            // transforming the data emitted by the Flow from one type or structure to another, while preserving the reactive nature of the stream
            .map { bookEntities ->   //  List<BookEntity> that was just emitted by the Flow
                bookEntities.map {  // return List<Book>
                    it.toBook()
                }
            }
    }

    override fun isBookFavorite(id: String): Flow<Boolean> {
        return favoriteBookDao
            .getFavoriteBooks()
            .map { bookEntities ->
                bookEntities.any { it.id == id }
            }
    }

    override suspend fun markAsFavorite(book: Book): EmptyResult<DataError.Local> {
        return try {
            favoriteBookDao.upsert(book.toBookEntity())
            Result.Success(Unit)
        }catch (e : SQLiteException){
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteFromFavorites(id: String) {
        favoriteBookDao.deleteFavoriteBook(id)
    }
}