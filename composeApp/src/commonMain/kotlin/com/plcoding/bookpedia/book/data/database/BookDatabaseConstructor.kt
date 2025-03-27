package com.plcoding.bookpedia.book.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")   // room will generate all implementations during compile time
expect object BookDatabaseConstructor: RoomDatabaseConstructor<FavoriteBookDatabase> {
    override fun initialize(): FavoriteBookDatabase
}

