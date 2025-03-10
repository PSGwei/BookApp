package com.plcoding.bookpedia.book.data.mappers

import com.plcoding.bookpedia.book.data.dto.SearchBookDto
import com.plcoding.bookpedia.book.domain.Book

// map data model to domain model

fun SearchBookDto.toBook(): Book{
    return Book(
        id = id,
        title = title,
        imageUrl = if(coverKey!=null){
            "https://covers.openlibrary.org/b/olid/${coverKey}-L.jpg"
        }else{
            "https://covers.openlibrary.org/b/olid/${coverAlternativeKey}-L.jpg"
        },
        authors = authorNames ?: emptyList(),
        description = null,
        languages = languages ?: emptyList(),
        firstPublishYear = firstPublishYear.toString(),
        averageRating = ratingsAverage,
        ratingCount = ratingsCount,
        numPages = numPagesMedian,
        numEditions = numEditions ?: 0,
    )
}