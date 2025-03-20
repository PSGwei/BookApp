package com.plcoding.bookpedia.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.plcoding.bookpedia.book.presentation.SelectedBookViewModel
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailAction
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailScreenRoot
import com.plcoding.bookpedia.book.presentation.book_detail.BookDetailViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.plcoding.bookpedia.book.presentation.book_list.BookListScreenRoot
import com.plcoding.bookpedia.book.presentation.book_list.BookListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Routes.BookGraph
        ){
            navigation<Routes.BookGraph>(
                startDestination = Routes.BookList
            ){
                composable<Routes.BookList> { entry ->
                    val viewModel = koinViewModel<BookListViewModel>()
                    val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)
                    // reset
                    LaunchedEffect(true){
                        selectedBookViewModel.onSelectBook(null)
                    }

                    BookListScreenRoot(
                        viewModel = viewModel,
                        onBookClick = { book ->
                            selectedBookViewModel.onSelectBook(book)
                            navController.navigate(Routes.BookDetail(book.id))
                        },
                    )
                }
                composable<Routes.BookDetail> { entry ->
//                    val args = entry.toRoute<Routes.BookDetail>()
                    val selectedBookViewModel = entry.sharedKoinViewModel<SelectedBookViewModel>(navController)
                    val selectedBook by selectedBookViewModel.selectedBook.collectAsStateWithLifecycle()
                    val viewModel = koinViewModel<BookDetailViewModel>()

                    LaunchedEffect(selectedBook){
                        selectedBook?.let {
                            viewModel.onAction(BookDetailAction.OnSelectedBookChange(selectedBook!!))
                        }
                    }

                    BookDetailScreenRoot(
                        viewModel = viewModel,
                        onBackClick = {
                            navController.navigateUp()
                        }
                    )
                }
            }
        }

    }
}

// share a ViewModel between different composables that belong to the same parent navigation graph
// retrieve a ViewModel that is scoped to a parent navigation graph, using Koin for dependency injection
@Composable
private inline fun <reified T:ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
) : T{
    // get the parent navigation graph route -> BookGraph in this case (used to scope ViewModel)
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    // Retrieve the parent NavBackStackEntry
    val parentEntry = remember(this){
        navController.getBackStackEntry(navGraphRoute)
    }
    //  retrieve the ViewModel from the Koin dependency injection container
    return koinViewModel(
        // makes the ViewModel scoped to the parent graph.
        viewModelStoreOwner = parentEntry
    )
}