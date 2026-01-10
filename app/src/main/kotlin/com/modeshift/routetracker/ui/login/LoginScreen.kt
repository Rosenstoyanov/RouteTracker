package com.modeshift.routetracker.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.modeshift.routetracker.R
import com.modeshift.routetracker.core.ui.utils.RtPreview
import com.modeshift.routetracker.core.ui.utils.debounceClick
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginAction
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginAction.OnLogin
import com.modeshift.routetracker.ui.login.LoginViewModel.LoginUiState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoginScreenContent(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {
    var userName by remember { mutableStateOf("") }
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .imePadding(),
        ) {
            Spacer(modifier = Modifier.weight(0.4f))

            Icon(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.weight(0.4f))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = userName,
                onValueChange = { userName = it },
                label = { Text(text = stringResource(id = R.string.user_name_hint)) },
                isError = uiState.userNameError != null,
                supportingText = { uiState.userNameError?.let { Text(text = it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onAction(OnLogin(userName)) },
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = debounceClick { onAction(OnLogin(userName)) }
            ) { Text(text = stringResource(id = R.string.login)) }

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    RtPreview {
        LoginScreenContent(
            uiState = LoginUiState(),
            onAction = {}
        )
    }
}