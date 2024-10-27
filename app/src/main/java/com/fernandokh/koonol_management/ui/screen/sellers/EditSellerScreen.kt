package com.fernandokh.koonol_management.ui.screen.sellers

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomDateField
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.MyUploadImage
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.sellers.EditSellerViewModel
import java.io.File

@Composable
fun EditSellersScreen(
    navController: NavHostController,
    sellerId: String?,
    viewModel: EditSellerViewModel = viewModel()
) {
    val isSeller by viewModel.isSeller.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    val imageUrl by viewModel.isPhoto.collectAsState()
    val isLoadingUpdate by viewModel.isLoadingUpdate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSeller(sellerId)
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.Sellers.route)
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
        topBar = { TopBarGoBack("Editar Vendedor", navController) },
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
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    isSeller == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontró el vendedor",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(0.dp, 16.dp, 0.dp, 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MyUploadImage(
                                directory = File(cacheDir, "images"),
                                url = imageUrl,
                                onSetImage = { viewModel.onPhotoChange(it) })
                            Spacer(modifier = Modifier.height(20.dp))
                            FormSeller(viewModel)

                            if (isShowDialog) {
                                AlertDialogC(
                                    dialogTitle = "Editar vendedor",
                                    dialogText = "¿Estás seguro de los nuevos datos para el vendedor?",
                                    onDismissRequest = { viewModel.dismissDialog() },
                                    onConfirmation = { viewModel.updateSeller() },
                                    loading = isLoadingUpdate
                                )
                            }
                        }
                    }
                }
            }
        },
    )

}


@Composable
private fun FormSeller(viewModel: EditSellerViewModel) {

    val formErrors by viewModel.formErrors.collectAsState()
    val name by viewModel.isName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val email by viewModel.email.collectAsState()
    val dayOfBirth by viewModel.dayOfBirth.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val phone by viewModel.phone.collectAsState()

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
        Text("Nombre", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            name,
            { viewModel.onNameChange(it) },
            "Ingresa tu nombre",
            error = formErrors.nameError != null,
            errorMessage = formErrors.nameError
        )
        Spacer(Modifier.height(16.dp))

        Text("Apellido", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            lastName,
            { viewModel.onLastNameChange(it) },
            "Ingresa tu apellido",
            error = formErrors.lastNameError != null,
            errorMessage = formErrors.lastNameError
        )
        Spacer(Modifier.height(16.dp))

        Text("Correo electrónico (opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            email,
            { viewModel.onEmailChange(it) },
            "ejemplo@gmail.com",
            KeyboardType.Email,
            error = formErrors.emailError != null,
            errorMessage = formErrors.emailError
        )
        Spacer(Modifier.height(16.dp))

        Text("Día de nacimiento", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomDateField(
            { viewModel.onDayOfBirthChange(it) },
            dayOfBirth,
            error = formErrors.birthdayError != null,
            errorMessage = formErrors.birthdayError
        )
        Spacer(Modifier.height(16.dp))

        Text("Género", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomSelect(
            options = viewModel.optionsGender,
            fill = false,
            selectedOption = gender,
            onOptionSelected = { viewModel.onGenderChange(it) },
            error = formErrors.genderError != null,
            errorMessage = formErrors.genderError
        )
        Spacer(Modifier.height(16.dp))

        Text("Número de celular (opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            phone,
            { viewModel.onPhoneChange(it) },
            "9991234567",
            KeyboardType.Phone,
            error = formErrors.phoneError != null,
            errorMessage = formErrors.phoneError
        )
    }
}
