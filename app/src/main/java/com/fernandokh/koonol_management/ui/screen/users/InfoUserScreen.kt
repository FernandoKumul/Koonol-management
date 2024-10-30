package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate
import com.fernandokh.koonol_management.viewModel.users.InfoUsersViewModel

@Composable
fun InfoUserScreen(
    navController: NavHostController,
    userId: String?,
    viewModel: InfoUsersViewModel = viewModel()
) {

    val isUser by viewModel.isUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUser(userId)
    }

    Scaffold(
        topBar = { TopBarGoBack("Usuario", navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    isUser == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontró el usuario",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    else -> {
                        InfoUser(isUser as UserInModel)
                    }
                }
            }
        },
    )
}

@Composable
private fun InfoUser(user: UserInModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 16.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user.photo != null) {
            AsyncImage(
                model = user.photo,
                contentDescription = "img_user",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(96.dp),
                placeholder = painterResource(R.drawable.default_user)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = "img_user",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(96.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
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
            InformationField("Rol", user.rol.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Nombre", user.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Apellido", user.lastName)
            Spacer(Modifier.height(24.dp))
            InformationField("Correo electrónico", user.email)
            Spacer(Modifier.height(24.dp))
            InformationField(
                "Día de nacimiento",
                formatIsoDateToLocalDate(user.birthday),
                imageVector = Icons.Filled.DateRange
            )
            Spacer(Modifier.height(24.dp))
            InformationField("Género", formatGender(user.gender))
            Spacer(Modifier.height(24.dp))
            InformationField("Número de celular", user.phoneNumber)
        }
    }
}

fun formatGender(gender: String): String {
    return when (gender) {
        "male" -> "Masculino"
        "female" -> "Femenino"
        else -> "Otro"
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PrevInfoUserScreen() {
    val navController = rememberNavController()
    KoonolmanagementTheme(dynamicColor = false) {
        InfoUserScreen(navController, "Id")
    }
}