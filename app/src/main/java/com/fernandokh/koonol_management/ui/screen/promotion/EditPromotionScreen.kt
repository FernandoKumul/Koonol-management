package com.fernandokh.koonol_management.ui.screen.promotion

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.utils.NavigationEvent
import com.fernandokh.koonol_management.viewModel.promotions.CreateEditPromotionViewModel
import com.fernandokh.koonol_management.viewModel.promotions.CreateEditPromotionViewModelFactory

@Composable
fun EditPromotionScreen(
    navController: NavHostController,
    promotionId: String?,
    tokenManager: TokenManager,
    viewModel: CreateEditPromotionViewModel = viewModel(
        factory = CreateEditPromotionViewModelFactory(tokenManager)
    )
) {
    val context = LocalContext.current

    val isLoadingEdit by viewModel.isLoadingEdit.collectAsState()
    val isLoadingSalesStalls by viewModel.isLoadingSalesStall.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isPromotion by viewModel.isPromotion.collectAsState()

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
            viewModel.getPromotion(token, promotionId ?: "")
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }
    Scaffold(
        topBar = { TopBarGoBack("Editar Promoción", navController) },
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
                isLoadingSalesStalls || isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                isPromotion == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontró la Promoción",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                else -> FormPromotion(viewModel)
            }

            if (isShowDialog) {
                AlertDialogC(
                    dialogTitle = "Editar Promoción",
                    dialogText = "¿Estás seguro de los datos para la Promoción?",
                    onDismissRequest = { viewModel.dismissDialog() },
                    onConfirmation = { viewModel.updatePromotion() },
                    loading = isLoadingEdit
                )
            }
        }
    }
}