package com.modeshift.routetracker.core

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.models.Resource.Failure

object Validators {
    fun validateAppUserName(userName: String): Resource<String> {
        return when {
            userName.isEmpty() -> Failure("Username cannot be empty")

            !userName.matches(Regex("^\\p{L}+$")) -> Failure("Username can only contain letters")

            else -> Resource.Success(userName)
        }
    }
}