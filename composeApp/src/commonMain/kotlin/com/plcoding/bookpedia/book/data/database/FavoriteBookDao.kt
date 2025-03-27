package com.plcoding.bookpedia.book.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {
    @Upsert // update + insert
    suspend fun upsert(book: BookEntity)

    @Query("SELECT * FROM BookEntity")
    // automatically trigger when data in table change
    // Flow is asynchronous by default, so no need add "suspend"
    fun getFavoriteBooks() : Flow<List<BookEntity>>

    @Query("SELECT * FROM BookEntity WHERE id = :id")
    suspend fun getFavoriteBook(id:String) : BookEntity?

    @Query("DELETE FROM BookEntity WHERE id = :id")
    suspend fun deleteFavoriteBook(id:String)
}

