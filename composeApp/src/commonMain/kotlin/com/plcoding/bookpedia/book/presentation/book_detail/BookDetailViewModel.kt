package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.Routes
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    // allow us to access navigation argument
    // It provides a key-value store that survives configuration changes and process death
    // directly tied to the ViewModel, don't need to manually save and restore state in onSaveInstanceState() and onCreate() (compare to Bundle)
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // extracts route information related to Routes.BookDetail from the savedStateHandle
    // type safety
    private val bookId = savedStateHandle.toRoute<Routes.BookDetail>().id
    private val _state = MutableStateFlow(BookDetailState())

    val state = _state
        .onStart {
            fetchBookDescription()
            observeFavoriteStatus()
        }
        .stateIn(
            viewModelScope,  // sharing of the flow will be active as long as the ViewModel is active
            // SharingStarted.WhileSubscribed:
                // start producing values when the first collector starts collecting
                // continue producing values as long as there is at least one collector
                // If there are no collectors for 5 seconds, the flow will become inactive
            SharingStarted.WhileSubscribed(5000L),
            _state.value    //  initial value of the StateFlow
        )

    fun onAction(action: BookDetailAction){
        when(action){
            BookDetailAction.OnBackClick -> {

            }
            BookDetailAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    if (state.value.isFavorite){
                        bookRepository.deleteFromFavorites(bookId)
                    }else{
                        state.value.book?.let { book ->
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }
            }
            is BookDetailAction.OnSelectedBookChange -> {
                _state.update {
                    it.copy(book = action.book)
                }
            }
        }
    }

    private fun observeFavoriteStatus(){
        bookRepository
            .isBookFavorite(bookId)
            // onEach: performing side effects for each value emitted by the upstream Flow without modifying the value itself
            .onEach { isFavorite ->
                _state.update {
                    it.copy(
                        isFavorite = isFavorite
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchBookDescription(){
        viewModelScope.launch {
            bookRepository
                .getBookDescription(bookId)
                .onSuccess { description ->
                    _state.update {
                        it.copy(
                            book = it.book?.copy(
                                description = description
                            ),
                            isLoading = false
                        )
                    }
                }
        }
    }
}