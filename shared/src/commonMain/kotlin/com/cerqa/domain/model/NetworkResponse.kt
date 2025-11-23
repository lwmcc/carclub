package com.cerqa.domain.model

/**
 * Represents the response from a network operation
 */
sealed class NetworkResponse<out T> {
    /**
     * No internet connection available
     */
    data object NoInternet : NetworkResponse<Nothing>()

    /**
     * Successful response with data
     */
    data class Success<out T>(val data: T?) : NetworkResponse<T>()

    /**
     * Error occurred during the operation
     */
    data class Error(val exception: Throwable) : NetworkResponse<Nothing>()
}
