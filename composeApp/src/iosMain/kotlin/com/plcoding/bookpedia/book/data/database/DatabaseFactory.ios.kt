package com.plcoding.bookpedia.book.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<FavoriteBookDatabase> {
        val dbFile = documentDirectory() + "/${FavoriteBookDatabase.DB_NAME}"
        return Room.databaseBuilder<FavoriteBookDatabase>(
            name = dbFile
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String{
        //accesses the default file manager object in iOS
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            // don't want to create the directory if it doesn't exist
            create = false,
            error = null
        )
        // checks if its argument is not null. If it is null, it throws an IllegalStateException
        return requireNotNull(documentDirectory?.path)
    }
}


