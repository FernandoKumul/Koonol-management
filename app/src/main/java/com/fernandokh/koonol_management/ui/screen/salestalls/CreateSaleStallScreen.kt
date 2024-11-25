package com.fernandokh.koonol_management.ui.screen.salestalls

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
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.DropdownInputForm
import com.fernandokh.koonol_management.ui.components.shared.UploadImage
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.categories.CategoriesViewModel
import com.fernandokh.koonol_management.viewModel.categories.CategoriesViewModelFactory
import com.fernandokh.koonol_management.viewModel.salesstalls.CreateSaleStallViewModel
import com.fernandokh.koonol_management.viewModel.sellers.SellersViewModel
import java.io.File

@Composable
fun CreateSaleStallScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    viewModel: CreateSaleStallViewModel = viewModel(),
    sellerViewModel: SellersViewModel = viewModel(),
    categoriesViewModel: CategoriesViewModel = viewModel(
        factory = CategoriesViewModelFactory(tokenManager)
    )
) {
    val context = LocalContext.current
    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        sellerViewModel.getAllSellers()
        categoriesViewModel.getAllSubcategories()
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.SalesStalls.route)
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
        topBar = { TopBarGoBack("Crear Puesto", navController) },
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
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(0.dp, 16.dp, 0.dp, 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FormSaleStall(viewModel, sellerViewModel, categoriesViewModel)

                if (isShowDialog) {
                    AlertDialogC(
                        dialogTitle = "Crear Puesto",
                        dialogText = "¿Estás seguro de los datos para el nuevo puesto?",
                        onDismissRequest = { viewModel.dismissDialog() },
                        onConfirmation = { viewModel.createSaleStall() },
                        loading = isLoadingCreate
                    )
                }

            }
        },
    )
}

@Composable
private fun FormSaleStall(
    viewModel: CreateSaleStallViewModel,
    sellerViewModel: SellersViewModel,
    categoriesViewModel: CategoriesViewModel
) {
    val context = LocalContext.current
    val cacheDir: File = context.cacheDir

    val formErrors by viewModel.formErrors.collectAsState()
    val name by viewModel.isName.collectAsState()
    val sellersList by sellerViewModel.sellersList.collectAsState()
    val sellerId by viewModel.sellerId.collectAsState()
    val subCategoriesList by categoriesViewModel.subCategoriesList.collectAsState()
    val subCategoryId by viewModel.subCategoryId.collectAsState()
    val description by viewModel.description.collectAsState()
    val type by viewModel.type.collectAsState()
    val principalPhoto by viewModel.isPrincipalPhoto.collectAsState()
    val secondPhoto by viewModel.isSecondPhoto.collectAsState()
    val thirdPhoto by viewModel.isThirdPhoto.collectAsState()
    val probationList = viewModel.probationOptions
    val probation by viewModel.probation.collectAsState()
    val activeList = viewModel.activeOptions
    val active by viewModel.active.collectAsState()

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
            "Ingresa el nombre del puesto",
            error = formErrors.nameError != null,
            errorMessage = formErrors.nameError
        )
        Spacer(Modifier.height(16.dp))
        DropdownInputForm(
            items = sellersList,
            selectedItem = sellersList.find { it.id == sellerId },
            onItemSelected = { selectedSeller ->
                viewModel.onSellerIdChange(selectedSeller.id)
            },
            itemLabel = { it.name },
            label = "Selecciona un vendedor",
        )
        Spacer(Modifier.height(16.dp))
        DropdownInputForm(
            items = subCategoriesList,
            selectedItem = subCategoriesList.find { it.id == subCategoryId },
            onItemSelected = { selectedSubCategory ->
                viewModel.onSubCategoryIdChange(selectedSubCategory.id)
            },
            itemLabel = { it.name },
            label = "Selecciona una subcategoría",
        )
        Spacer(Modifier.height(16.dp))
        Text("Tipo", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            type,
            { viewModel.onTypeChange(it) },
            "Ingresa el tipo del puesto",
            error = formErrors.typeError != null,
            errorMessage = formErrors.typeError
        )
        Spacer(Modifier.height(16.dp))
        DropdownInputForm(
            items = probationList,
            selectedItem = probationList.find { it.value == probation },
            onItemSelected = { selectedProbation ->
                viewModel.onProbationChange(selectedProbation.value)
            },
            itemLabel = { it.name },
            label = "Selecciona si el puesto es de prueba",
        )
        Spacer(Modifier.height(16.dp))
        DropdownInputForm(
            items = activeList,
            selectedItem = activeList.find { it.value == active },
            onItemSelected = { selectedActive ->
                viewModel.onActiveChange(selectedActive.value)
            },
            itemLabel = { it.name },
            label = "Selecciona el estado del puesto",
        )
        Spacer(Modifier.height(16.dp))
        Text("Descripción", color = MaterialTheme.colorScheme.onSurfaceVariant)
        CustomTextField(
            description,
            { viewModel.onDescriptionChange(it) },
            "Ingresa la descripción del puesto",
            error = formErrors.descriptionError != null,
            errorMessage = formErrors.descriptionError
        )
        Spacer(Modifier.height(16.dp))
        Text("Imagen principal", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        UploadImage(
            directory = File(cacheDir, "images"),
            url = principalPhoto,
            onSetImage = { viewModel.onPrincipalPhotoChange(it) })
        Spacer(Modifier.height(16.dp))
        Text("Imagenes secundarias", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        UploadImage(
            directory = File(cacheDir, "images"),
            url = secondPhoto,
            onSetImage = { viewModel.onSecondPhotoChange(it) })
        Spacer(Modifier.height(16.dp))
        UploadImage(
            directory = File(cacheDir, "images"),
            url = thirdPhoto,
            onSetImage = { viewModel.onThirdPhotoChange(it) })
    }
}