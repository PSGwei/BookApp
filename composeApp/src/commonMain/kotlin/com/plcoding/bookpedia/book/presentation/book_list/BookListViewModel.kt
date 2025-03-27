package com.plcoding.bookpedia.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BookListState())
    val state = _state
        // Flow operator
        // execute a block of code when the flow starts emitting values.
        .onStart {
            if (cachedBooks.isEmpty()) {
                observeSearchQuery()
            }
            // when we navigate to details screen, this is still observing, because this run in its own viewmodescope,
            // if we come back to list screen after 5s, this will add another new observer
            // that's why we need to cancel the job
            observeFavoriteBooks()
        }
        // convert a cold flow into a state flow that can be observed in the ViewModel
        // SharingStarted is an enum that controls how the flow behaves when it's being collected by consumers.
        // WhileSubscribed: flow will stay active and share the emissions while there is at least one active subscriber to the flow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private val cachedBooks: List<Book> = emptyList()
    private var searchJob: Job? = null
    private var observeFavoriteBookJob: Job? = null

    fun onAction(action: BookListAction) {
        when (action) {
            is BookListAction.OnBookClick -> {

            }

            is BookListAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(searchQuery = action.query)
                }
            }

            is BookListAction.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }
        }
    }

    private fun observeFavoriteBooks() {
        observeFavoriteBookJob?.cancel()
        observeFavoriteBookJob = bookRepository
            .getFavoritesBooks()
            .onEach { favoriteBooks ->
                _state.update {
                    it.copy(
                        favouriteBooks = favoriteBooks
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        state
            // extract just the searchQuery string from each emission of BookListState
            // return Flow<String>
            .map {
                it.searchQuery
            }
            // If the new value is the same as the last one, it won't be passed downstream
            .distinctUntilChanged()
            // delay between emissions
            // if emitted faster than the debounce interval. it “resets” its timer for each new emission
            .debounce(500L)
            // perform side effects for each value the flow emits
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update {
                            it.copy(
                                errorMessage = null,
                                searchResults = cachedBooks
                            )
                        }
                    }

                    query.length > 2 -> {
                        searchJob?.cancel()
                        searchJob = searchBooks(query)
                    }
                }
            }
            // ensures that the flow collection is tied to the ViewModel’s lifecycle
            .launchIn(viewModelScope)
    }

    private fun searchBooks(query: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true
            )
        }

        bookRepository
            .searchBooks(query)
            .onSuccess { searchResults ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        searchResults = searchResults
                    )
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        searchResults = emptyList(),
                        errorMessage = error.toUiText()
                    )
                }
            }
    }
}

