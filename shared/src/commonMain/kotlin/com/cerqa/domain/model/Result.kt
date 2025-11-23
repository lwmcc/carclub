package com.cerqa.domain.model

import kotlinx.serialization.Serializable

/**
 * Generic result type with pending state
 */
sealed class NetResult<out T> {
    data object Pending : NetResult<Nothing>()
    data class Success<out T>(val data: T?) : NetResult<T>()
    data class Error(val exception: Throwable) : NetResult<Nothing>()
}

/**
 * Network result type with internet connectivity status
 */
sealed class NetWorkResult<out T> {
    data class Success<out T>(val data: T?) : NetWorkResult<T>()
    data class Error(val exception: Throwable) : NetWorkResult<Nothing>()
    data object NoInternet : NetWorkResult<Nothing>()
}

/**
 * Search result type with idle state
 */
sealed class NetSearchResult<out T> {
    data object Pending : NetSearchResult<Nothing>()
    data object NoInternet : NetSearchResult<Nothing>()
    data object Idle : NetSearchResult<Nothing>()
    data class Success<out T>(val data: T?) : NetSearchResult<T>()
    data class Error(val exception: Throwable) : NetSearchResult<Nothing>()
}

/**
 * UI state result type
 */
sealed class UiStateResult<out T> {
    data object Pending : UiStateResult<Nothing>()
    data object NoInternet : UiStateResult<Nothing>()
    data object Idle : UiStateResult<Nothing>()
    data class Success<out T>(val data: T?) : UiStateResult<T>()
    data class Error(val exception: Throwable) : UiStateResult<Nothing>()
}

/**
 * Delete operation result type
 */
sealed class NetDeleteResult {
    data object Pending : NetDeleteResult()
    data object Success : NetDeleteResult()
    data object NoInternet : NetDeleteResult()
    data class Error(val exception: Throwable) : NetDeleteResult()
}
