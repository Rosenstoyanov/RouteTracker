package com.modeshift.routetracker.ui.login

import androidx.lifecycle.viewModelScope
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.navigation.NavTarget.Companion.withCleanStack
import com.modeshift.routetracker.navigation.NavTarget.RouteTracking
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginAction
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginAction.OnLogin
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appUserNameStore: AppUserNameStore,
    private val navigator: Navigator
) : BaseViewModel<LoginUiState, LoginAction>(LoginUiState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is OnLogin -> viewModelScope.launch {
                val userName = action.username.trim()
                when {
                    userName.isEmpty() -> updateState {
                        it.copy(userNameError = "Username cannot be empty")
                    }

                    !userName.matches(Regex("^\\p{L}+$")) -> updateState {
                        it.copy(userNameError = "Username can only contain letters")
                    }

                    else -> {
                        appUserNameStore.storeUserName(userName)
                        navigator.navigate(
                            navTarget = RouteTracking,
                            builder = withCleanStack()
                        )
                    }
                }
            }
        }
    }

    data class LoginUiState(
        val userNameError: String? = null
    )

    sealed interface LoginAction {
        data class OnLogin(val username: String) : LoginAction
    }
}