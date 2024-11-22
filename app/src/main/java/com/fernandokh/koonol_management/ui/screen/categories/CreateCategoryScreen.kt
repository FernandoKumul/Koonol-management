package com.fernandokh.koonol_management.ui.screen.categories

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.categories.CreateCategoryViewModel
import com.fernandokh.koonol_management.viewModel.categories.CreateCategoryViewModelFactory

@Composable
fun CreateCategoryScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    viewModel: CreateCategoryViewModel = viewModel(
        factory = CreateCategoryViewModelFactory(tokenManager)
    )
) {

    val context = LocalContext.current

    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.Categories.route)
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
        topBar = { TopBarGoBack("Crear Categoría", navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val isValid = viewModel.isFormValid()
                    if (isValid) {
                        viewModel.showDialog()
                    } else {
                        Toast.makeText(context, "Error al validar", Toast.LENGTH_SHORT).show()
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormCategory(viewModel)

                if (isShowDialog) {
                    AlertDialogC(
                        dialogTitle = "Crear categoría",
                        dialogText = "¿Estás seguro de los datos para la nueva categoría?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = { viewModel.createCategory() },
                        loading = isLoadingCreate
                    )
                }
            }

        },
    )
}

@Composable
private fun FormCategory(viewModel: CreateCategoryViewModel) {

    val formErrors by viewModel.formErrors.collectAsState()
    val form by viewModel.form.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 36.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                form.name,
                { viewModel.onNameCategoryChange(it) },
                "Ingresa el nombre de la categoría",
                error = formErrors.name != null,
                errorMessage = formErrors.name
            )

            Spacer(Modifier.height(16.dp))

            Text("Tarifa recomendada", color = MaterialTheme.colorScheme.onSurfaceVariant)
            CustomTextField(
                form.recommendedRate,
                { viewModel.onRecommendedRateChange(it) },
                "$0.00",
                keyboardType = KeyboardType.Phone,
                error = formErrors.recommendedRate != null,
                errorMessage = formErrors.recommendedRate
            )
        }
        Spacer(Modifier.height(20.dp))

        ListSubcategories(viewModel)
    }
}

@Composable
private fun ListSubcategories(
    viewModel: CreateCategoryViewModel,
) {
    val subcategories by viewModel.subcategories.collectAsState()

    Box(Modifier.wrapContentSize()) {
        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            onClick = { viewModel.addSubcategory() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(40.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "add_icon",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

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
            Text(
                "Subcategorías",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            if (subcategories.isEmpty()) {
                Text("No tiene ninguna categoría")
            } else {
                Column(
                    Modifier.wrapContentHeight()
                ) {
                    subcategories.forEachIndexed { index, subcategory ->
                        CustomTextField(
                            text = subcategory.name,
                            onTextChange =  { viewModel.onSubCategoryChange(it, index) },
                            "",
                            error = subcategory.error != null,
                            errorMessage = subcategory.error,
                            icon = Icons.Default.Close,
                            iconAction = { viewModel.removeSubcategory(subcategory.id) }
                        )
                        if (index != subcategories.size - 1) {
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun PrevInfoCategoryScreen() {
    KoonolmanagementTheme(dynamicColor = false) {
        val navController = rememberNavController()
        CreateCategoryScreen(navController, TokenManager(LocalContext.current))
    }
}