package com.modeshift.routetracker.core.models

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO: Ross update errors to handle well localisation
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
}

inline fun <T, R> Resource<T>.map(
    transformSuccess: (T) -> R,
): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transformSuccess(this.data))
        is Resource.Error -> Resource.Error(this.message)
    }
}

inline fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> {
    return when (this) {
        is Resource.Success -> transform(this.data)
        is Resource.Error -> Resource.Error(this.message)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Resource<T>.onFailure(action: (exception: Resource.Error<T>) -> Unit): Resource<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    (this as? Resource.Error<T>)?.let {
        action(it)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Resource<T>.onSuccess(action: (value: Resource.Success<T>) -> Unit): Resource<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    (this as? Resource.Success<T>)?.let {
        action(it)
    }
    return this
}