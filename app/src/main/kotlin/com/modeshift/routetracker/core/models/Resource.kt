package com.modeshift.routetracker.core.models

import com.modeshift.routetracker.core.models.Resource.Failure
import com.modeshift.routetracker.core.models.Resource.Success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO: update Failure argument to handle well localisation
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val message: String) : Resource<Nothing>()
}

inline fun <T, R> Resource<T>.map(
    transformSuccess: (T) -> R,
): Resource<R> {
    return when (this) {
        is Success -> Success(transformSuccess(this.data))
        is Failure -> Failure(this.message)
    }
}

inline fun <T, R> Resource<T>.flatMap(transform: (T) -> Resource<R>): Resource<R> {
    return when (this) {
        is Success -> transform(this.data)
        is Failure -> Failure(this.message)
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Resource<T>.onFailure(action: (exception: Failure) -> Unit): Resource<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    (this as? Failure)?.let {
        action(it)
    }
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T> Resource<T>.onSuccess(action: (value: Success<T>) -> Unit): Resource<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    (this as? Success<T>)?.let {
        action(it)
    }
    return this
}