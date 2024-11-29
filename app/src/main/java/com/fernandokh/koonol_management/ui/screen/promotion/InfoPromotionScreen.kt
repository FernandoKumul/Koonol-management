package com.fernandokh.koonol_management.ui.screen.promotion

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.data.models.PromotionAndNameModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate
import com.fernandokh.koonol_management.viewModel.promotions.InfoPromotionViewModel

@Composable
fun InfoPromotionScreen(
    navController: NavHostController,
    promotionId: String?,
    tokenManager: TokenManager,
    viewModel: InfoPromotionViewModel = viewModel()
) {
    val isPromotion by viewModel.isPromotion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.getPromotion(token, promotionId ?: "")
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Promoción", navController) },
    ) { innerPadding ->
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

                isPromotion == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontró la promoción",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                else -> InfoPromotion(isPromotion!!)
            }

        }
    }
}

@Composable
private fun InfoPromotion(promotion: PromotionAndNameModel) {
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
            InformationField("Nombre del puesto", promotion.salesStallId.name)
            Spacer(Modifier.height(24.dp))
            InformationField(
                "Fecha de inicio",
                formatIsoDateToLocalDate(promotion.startDate),
                imageVector = Icons.Filled.DateRange
            )
            Spacer(Modifier.height(24.dp))
            InformationField(
                "Fecha de finalización",
                formatIsoDateToLocalDate(promotion.endDate),
                imageVector = Icons.Filled.DateRange
            )
            Spacer(Modifier.height(24.dp))
            InformationField("Pago", promotion.pay.toString())
        }
    }
}

@Preview
@Composable
private fun PrevProfileScreen() {
    KoonolmanagementTheme(dynamicColor = false) {
        val navController = rememberNavController()
        InfoPromotionScreen(navController, "", TokenManager(LocalContext.current))
    }
}
