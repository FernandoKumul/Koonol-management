package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomDateField
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.MyUploadImage
import com.fernandokh.koonol_management.ui.components.shared.PasswordInput
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.SelectOption
import com.fernandokh.koonol_management.viewModel.users.CreateUserViewModel

@Composable
fun CreateUserScreen(navController: NavHostController, viewModel: CreateUserViewModel = viewModel()) {
    //val context = LocalContext.current
    //val cacheDir: File = context.cacheDir

    var imageUrl by remember { mutableStateOf<String?>(null) }
    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    Scaffold(
        topBar = { TopBarGoBack("Crear Usuario", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() },
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MyUploadImage(
                    //directory = File(cacheDir, "images"),
                    url = imageUrl,
                    onSetImage = { imageUrl = it })
                Spacer(modifier = Modifier.height(20.dp))
                FormUser()

                if (isShowDialog) {
                    AlertDialogC(
                        dialogTitle = "Crear usuario",
                        dialogText = "¿Estás seguro de los datos para el nuevo usuario?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = { viewModel.createUser() },
                        loading = isLoadingCreate
                    )
                }

            }
        },
    )
}

@Composable
private fun FormUser () {
    val optionsRol = listOf(
        SelectOption("Selecciona un rol", ""),
        SelectOption("Administrador", "670318104d9824b4da0d9a9b"),
        SelectOption("Gestor", "6704214d834d7e5203cc834d")
    )

    val optionsGender = listOf(
        SelectOption("Selecciona un género", ""),
        SelectOption("Masculino", "male"),
        SelectOption("Fenemino", "female"),
        SelectOption("Otro", "other")
    )

    var rol by remember { mutableStateOf(optionsRol[0]) }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dayOfBirth by remember { mutableStateOf<String?>(null) }
    var gender by remember { mutableStateOf(optionsGender[0]) }
    var phone by remember { mutableStateOf("") }

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


        Text("Rol", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomSelect(
            options = optionsRol,
            fill = false,
            selectedOption = rol,
            onOptionSelected = { rol = it }
        )
        Spacer(Modifier.height(16.dp))

        Text("Nombre", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(name, { name = it }, "Ingresa tu nombre")
        Spacer(Modifier.height(16.dp))

        Text("Apellido", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(lastName, { lastName = it }, "Ingresa tu apellido")
        Spacer(Modifier.height(16.dp))

        Text("Correo electrónico", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(email, { email = it }, "ejemplo@gmail.com", KeyboardType.Email)
        Spacer(Modifier.height(16.dp))

        Text("Contraseña", color = MaterialTheme.colorScheme.onSurfaceVariant)
        PasswordInput(password, { password = it }, "Ingresa una contraseña segura")
        Spacer(Modifier.height(16.dp))

        Text("Día de nacimiento", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomDateField({ dayOfBirth = it }, dayOfBirth)
        Spacer(Modifier.height(16.dp))

        Text("Género", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomSelect(
            options = optionsGender,
            fill = false,
            selectedOption = gender,
            onOptionSelected = { gender = it }
        )
        Spacer(Modifier.height(16.dp))

        Text("Número de celular", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(phone, { phone = it }, "999 123 45678", KeyboardType.Phone)
    }

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PrevCreateUserScreen() {
    val navController = rememberNavController()
    KoonolmanagementTheme(dynamicColor = false) {
        CreateUserScreen(navController)
    }
}