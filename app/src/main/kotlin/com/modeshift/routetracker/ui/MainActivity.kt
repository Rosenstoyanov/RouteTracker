package com.modeshift.routetracker.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.modeshift.routetracker.data.store.AppUserNameStore
import com.modeshift.routetracker.core.BaseViewModel
import com.modeshift.routetracker.core.ui.theme.RtTheme
import com.modeshift.routetracker.core.ui.utils.ObserveAsEvents
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.navigation.AppNavigation
import com.modeshift.routetracker.navigation.NavDirection.GoBack
import com.modeshift.routetracker.navigation.NavDirection.NavigateTo
import com.modeshift.routetracker.navigation.NavTarget
import com.modeshift.routetracker.navigation.NavTarget.Companion.withCleanStack
import com.modeshift.routetracker.navigation.NavTarget.Login
import com.modeshift.routetracker.navigation.NavTarget.RouteTracking
import com.modeshift.routetracker.navigation.Navigator
import com.modeshift.routetracker.ui.AppViewModel.AppState.Initialized
import com.modeshift.routetracker.ui.AppViewModel.AppState.Initializing
import com.modeshift.routetracker.ui.AppViewModel.AppUiState
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator
    private val viewModel: AppViewModel by viewModels()

    private var isInitialising = true

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { isInitialising }
        setContent {
            RtTheme {
                val navController = rememberNavController()
                ObserveAsEvents(navigator.directions) {
                    if (it == navController.currentDestination) {
                        return@ObserveAsEvents
                    }

                    when (it) {
                        GoBack -> navController.popBackStack()
                        is NavigateTo -> navController.navigate(
                            route = it.target,
                            builder = it.builder
                        )
                    }
                }
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(uiState.appState) {
                    when (val appState = uiState.appState) {
                        is Initialized -> {
                            navController.navigate(
                                route = appState.target,
                                builder = withCleanStack()
                            )
                            isInitialising = false
                        }

                        Initializing -> isInitialising = true
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationPermissionState = rememberPermissionState(
                        permission = Manifest.permission.POST_NOTIFICATIONS,
                        onPermissionResult = {}
                    )
                    LaunchedEffect(Unit) { notificationPermissionState.launchPermissionRequest() }
                }

                AppNavigation(
                    navController = navController,
                    startDestination = Login
                )
            }
        }
    }
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appUserNameStore: AppUserNameStore,
    @AppScope
    private val appScope: CoroutineScope
) : BaseViewModel<AppUiState, Unit>(AppUiState()) {
    init {
        appScope.launch {
            val target = appUserNameStore.loadAppUserName()?.let { RouteTracking } ?: Login
            updateState {
                it.copy(appState = Initialized(target))
            }
        }
    }

    data class AppUiState(
        val appState: AppState = Initializing
    )

    sealed interface AppState {
        data object Initializing : AppState
        data class Initialized(val target: NavTarget) : AppState
    }
}