package com.plcoding.bookpedia.core.domain


sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: com.plcoding.bookpedia.core.domain.Error>(val error: E):
        Result<Nothing, E>
}

// transform the data inside a Result.Success without altering the error type
//T: The type of the successful result (the data in Result.Success).
//E: The type of the error (the error in Result.Error). It's constrained to be a subclass of Error (i.e., it must be some kind of error type).
//R: The new type you want to map the data to. It represents the result after transformation
//map: (T) -> R: defines how to transform the value inside Result.Success from type T to type R
//this: refers to the Result<T, E> object
//Result.Success(map(data)):  If the result is a success (Result.Success), it transforms the data inside the success using the provided map function
inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  }
}

inline fun <T, E: Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}
inline fun <T, E: Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error)
            this
        }
        is Result.Success -> this
    }
}

typealias EmptyResult<E> = Result<Unit, E>



//typealias RootError = Error     // to avoid the name crash (data class Error)
// "Result" takes two generic type parameters (D,E)
// D and E: allow to pass any type of data and error that implement RootError
// "out" means the it only can be used as a return type
/*
sealed interface Result<out D, out E:RootError>{
    data class Success<out D, out E:RootError>(val data:D): Result<D,E>
    data class Error<out D, out E: RootError>(val error: E) : Result<D,E>
}*/
