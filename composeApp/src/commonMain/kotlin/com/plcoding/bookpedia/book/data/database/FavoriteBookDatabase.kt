package com.plcoding.bookpedia.book.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [BookEntity::class],
    version = 1
)

@TypeConverters(
    StringListTypeConverter::class
)
abstract class FavoriteBookDatabase : RoomDatabase(){
    abstract val favoriteBookDao: FavoriteBookDao

    // because this is abstract class, we cannot use normal variable
    companion object {
        const val DB_NAME = "book.db"
    }
}



