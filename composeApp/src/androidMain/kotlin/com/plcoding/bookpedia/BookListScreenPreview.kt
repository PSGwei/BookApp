package com.plcoding.bookpedia

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.presentation.book_list.BookListScreen
import com.plcoding.bookpedia.book.presentation.book_list.BookListState
import com.plcoding.bookpedia.book.presentation.book_list.components.BookSearchBar

private val books = (1..100).map {
    Book(
        id = it.toString(),
        title = "Book $it",
        imageUrl = "https://example.com",
        authors = listOf("abu"),
        description = "Book description",
        languages = emptyList(),
        firstPublishYear = null,
        averageRating = 3.844,
        ratingCount = null,
        numPages = null,
        numEditions = 1
    )
}

@Preview(showBackground = true)
@Composable
private fun BookListScreenPreview() {
    BookListScreen(
        state = BookListState(
            searchResults = books
        ),
        onAction = {}
    )
}