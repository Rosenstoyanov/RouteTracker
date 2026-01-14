package com.modeshift.routetracker.core.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

fun <T> MutableStateFlow<T>.updateAndLog(source: String? = null, function: (T) -> T) {
    val oldValue = value
    this.update(function)
    Timber.i("${source?.let { "$source: " } ?: ""}State updated from:\n$oldValue\nto\n$value")
}