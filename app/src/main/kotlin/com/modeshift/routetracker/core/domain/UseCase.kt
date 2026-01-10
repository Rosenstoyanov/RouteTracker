package com.modeshift.routetracker.core.domain

interface UseCase<in Param, out Result> {
    suspend operator fun invoke(params: Param): Result {
        return execute(params)
    }

    suspend fun execute(params: Param): Result
}