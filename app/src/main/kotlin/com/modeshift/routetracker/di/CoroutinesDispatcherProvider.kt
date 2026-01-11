package com.modeshift.routetracker.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val mainImmediate: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher,
) {
    @Inject
    constructor() : this(
        main = Dispatchers.Main,
        mainImmediate = Dispatchers.Main.immediate,
        computation = Dispatchers.Default,
        io = Dispatchers.IO
    )
}