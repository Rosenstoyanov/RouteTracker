package com.modeshift.routetracker.core

import com.modeshift.routetracker.core.Validators.validateAppUserName
import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.models.Resource.Failure
import com.modeshift.routetracker.core.models.Resource.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class ValidatorsTest {
    @ParameterizedTest
    @MethodSource("provideErrorsForUserNameArguments")
    fun `appUserName is correctly validated`(appUserName: String, expected: Resource<*>)  {
        val actual = validateAppUserName(appUserName)

        assertEquals(expected, actual)
    }

    companion object {
        const val USERNAME_CAN_NOT_BE_EMPTY = "Username cannot be empty"
        const val USERNAME_CAN_CONTAINS_LETTERS_ONLY = "Username can only contain letters"

        @JvmStatic
        fun provideErrorsForUserNameArguments() = listOf(
            arguments("", Failure(USERNAME_CAN_NOT_BE_EMPTY)),
            arguments("123asd", Failure(USERNAME_CAN_CONTAINS_LETTERS_ONLY)),
            arguments("!!££@345asd", Failure(USERNAME_CAN_CONTAINS_LETTERS_ONLY)),
            arguments("BusR", Success("BusR"))
        )
    }
}