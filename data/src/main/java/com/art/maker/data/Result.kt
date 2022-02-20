package com.art.maker.data

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: kotlin.Exception) : Result<Nothing>()
    data class Exception(val code: Int, val msg: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Exception -> "Exception[code=$code msg=$msg]"
            Loading -> "Loading"
        }
    }
}
