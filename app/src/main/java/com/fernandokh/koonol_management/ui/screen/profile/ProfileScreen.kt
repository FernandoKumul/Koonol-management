package com.fernandokh.koonol_management.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.screen.users.formatGender
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate
import com.fernandokh.koonol_management.viewModel.profile.ProfileViewModel
import com.fernandokh.koonol_management.viewModel.profile.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    tokenManager: TokenManager
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(tokenManager)
    )

    val isUser by viewModel.isUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.getUser(token)
        }
    }

    Scaffold(
        topBar = { TopBarMenuTitle("Perfil", drawerState) },
        floatingActionButton = {
            if (isUser != null && !isLoading) {
                Row {
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.ChangePassword.route) },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            painterResource(R.drawable.baseline_key_24),
                            contentDescription = "Add"
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.EditProfile.route) },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(painterResource(R.drawable.ic_edit_2_line), contentDescription = "Add")
                    }
                }
            }
        },
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
                        InfoProfile(isUser!!)
                    }
                }
            }
        },
    )
}

@Composable
private fun InfoProfile(user: UserInModel) {
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


@Preview
@Composable
private fun PrevProfileScreen() {
    KoonolmanagementTheme(dynamicColor = false) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        ProfileScreen(navController, drawerState, TokenManager(LocalContext.current))
    }
}
