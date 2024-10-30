package com.fernandokh.koonol_management.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.PasswordInput
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.profile.ChangePasswordViewModel
import com.fernandokh.koonol_management.viewModel.profile.ChangePasswordViewModelFactory

@Composable
fun ChangePasswordScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
) {

    val viewModel: ChangePasswordViewModel = viewModel(
        factory = ChangePasswordViewModelFactory(tokenManager)
    )

    val token by viewModel.accessToken.collectAsState()

    val isLoadingUpdate by viewModel.isLoadingUpdate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()
    val context = LocalContext.current

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.Profile.route)
                }
            }
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Cambiar contraseña", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isValid = viewModel.isFormValid()
                    if (isValid) {
                        viewModel.showDialog()
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(painter = painterResource(R.drawable.ic_save_line), contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(0.dp, 16.dp, 0.dp, 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PasswordFormComposable(viewModel)
                    if (isShowDialog) {
                        AlertDialogC(
                            dialogTitle = "Editar usuario",
                            dialogText = "¿Estás seguro de los nuevos datos para el usuario?",
                            onDismissRequest = { viewModel.dismissDialog() },
                            onConfirmation = { viewModel.changePassword(token) },
                            loading = isLoadingUpdate
                        )
                    }
                }
            }
        },

        )
}

@Composable
private fun PasswordFormComposable(viewModel: ChangePasswordViewModel) {

    val formErrors by viewModel.formErrors.collectAsState()
    val form by viewModel.form.collectAsState()

    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp, 24.dp)
            .fillMaxWidth(0.8f)
    ) {
        Text("Contraseña", color = MaterialTheme.colorScheme.onSurfaceVariant)
        PasswordInput(
            form.password,
            { viewModel.onPasswordChange(it) },
            "Ingresa una contraseña segura",
            error = formErrors.password != null,
            errorMessage = formErrors.password
        )
        Spacer(Modifier.height(16.dp))

        Text("Confirmar contraseña", color = MaterialTheme.colorScheme.onSurfaceVariant)
        PasswordInput(
            form.confirmPassword,
            { viewModel.onConfirmPasswordChange(it) },
            "Confirma la contraseña",
            error = formErrors.confirmPassword != null,
            errorMessage = formErrors.confirmPassword
        )
    }

}