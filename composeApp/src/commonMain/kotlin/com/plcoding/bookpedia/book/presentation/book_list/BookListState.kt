package com.plcoding.bookpedia.book.presentation.book_list

import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.presentation.UiText

data class BookListState(
    val searchQuery:String = "Kotlin",
    val searchResults:List<Book> = emptyList(),
    val favouriteBooks:List<Book> = emptyList(),
    val isLoading:Boolean = false,
    val selectedTabIndex:Int = 0,
    val errorMessage:UiText? = null
)
//
//private val books = (1..100).map {
//    Book(
//        id = it.toString(),
//        title = "Book $it",
//        imageUrl = "https://example.com",
//        authors = listOf("abu"),
//        description = "Book description",
//        languages = emptyList(),
//        firstPublishYear = null,
//        averageRating = 3.844,
//        ratingCount = null,
//        numPages = null,
//        numEditions = 1
//    )
//}