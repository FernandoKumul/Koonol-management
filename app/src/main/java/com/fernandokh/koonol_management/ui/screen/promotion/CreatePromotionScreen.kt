package com.fernandokh.koonol_management.ui.screen.promotion

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
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomDateField
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.promotions.CreateEditPromotionViewModel
import com.fernandokh.koonol_management.viewModel.promotions.CreateEditPromotionViewModelFactory

@Composable
fun CreatePromotionScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    viewModel: CreateEditPromotionViewModel = viewModel(
        factory = CreateEditPromotionViewModelFactory(tokenManager)
    )
) {
    val context = LocalContext.current

    val isLoadingCreate by viewModel.isLoadingCreate.collectAsState()
    val isLoadingSalesStalls by viewModel.isLoadingSalesStall.collectAsState()
    val isShowDialog by viewModel.isShowDialog.collectAsState()

    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.Navigate -> {
                    navController.navigate(Screen.Promotions.route)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.getSalesStall(token)
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Crear Promoción", navController) },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoadingSalesStalls -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> FormPromotion(viewModel)
            }

            if (isShowDialog) {
                AlertDialogC(
                    dialogTitle = "Crear Promoción",
                    dialogText = "¿Estás seguro de los datos para la nueva Promoción?",
                    onDismissRequest = { viewModel.dismissDialog() },
                    onConfirmation = { viewModel.createPromotion() },
                    loading = isLoadingCreate
                )
            }
        }
    }
}

@Composable
fun FormPromotion(viewModel: CreateEditPromotionViewModel) {
    val formErrors by viewModel.formErrors.collectAsState()
    val form by viewModel.form.collectAsState()
    val salesStallOptions by viewModel.isSalesStallOptions.collectAsState()

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
            CustomSelect(
                options = salesStallOptions,
                fill = false,
                selectedOption = form.salesStallIdOption,
                onOptionSelected = { viewModel.onSalesStallOptionChange(it) },
                error = formErrors.salesStallIdOption != null,
                errorMessage = formErrors.salesStallIdOption
            )
            Spacer(Modifier.height(16.dp))

            Text("Fecha de inicio", color = MaterialTheme.colorScheme.onSurfaceVariant)
            CustomDateField(
                { viewModel.onStartDateChange(it) },
                form.startDate,
                error = formErrors.startDate != null,
                errorMessage = formErrors.startDate
            )
            Spacer(Modifier.height(16.dp))

            Text("Fecha de finalización", color = MaterialTheme.colorScheme.onSurfaceVariant)
            CustomDateField(
                { viewModel.onEndDateChange(it) },
                form.endDate,
                error = formErrors.endDate != null,
                errorMessage = formErrors.endDate
            )
            Spacer(Modifier.height(16.dp))

            Text("Pago", color = MaterialTheme.colorScheme.onSurfaceVariant)
            CustomTextField(
                form.pay,
                { viewModel.onPayChange(it) },
                "$0.00",
                keyboardType = KeyboardType.Phone,
                error = formErrors.pay != null,
                errorMessage = formErrors.pay
            )
        }
        Spacer(Modifier.height(20.dp))
    }
}