package com.plcoding.bookpedia.book.data.network

import com.plcoding.bookpedia.book.data.dto.SearchResponseDto
import com.plcoding.bookpedia.core.data.safeCall
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://openlibrary.org"

// When safeCall is invoked, Kotlin will infer the type of T based on the expected return type of searchBooks
class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) : RemoteBookDataSource{
    override suspend fun searchBooks(
        query: String,
        resultLimit: Int?
    ): Result<SearchResponseDto, DataError.Remote> {
        return safeCall<SearchResponseDto> {
            httpClient.get(
                urlString = "$BASE_URL/search.json"
            ) {
                // parameter:  DSL (Domain-Specific Language) function used to add or modify a query parameter on the outgoing request.
                // GET https://openlibrary.org/search.json?q=<query>&limit=<resultLimit>&language=eng&fields=...
                parameter("q", query)
                parameter("limit", resultLimit)
                parameter("language", "eng")
                parameter(
                    "fields",
                    "key,title,author,language,cover_i,author_key,author_name,cover_edition_key," +
                    "first_publish_year,ratings_average,ratings_count,number_of_pages_median,edition_count"
                )
            }
        }
    }
}
