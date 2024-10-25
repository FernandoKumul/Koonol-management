package com.fernandokh.koonol_management.ui.screen.sellers

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.screen.users.InformationField
import com.fernandokh.koonol_management.ui.screen.users.formatGender
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate
import com.fernandokh.koonol_management.viewModel.sellers.InfoSellerViewModel

@Composable
fun InfoSellersScreen(
    navController: NavHostController,
    sellerId: String?,
    viewModel: InfoSellerViewModel = viewModel()
) {
    val isSeller by viewModel.isSeller.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSeller(sellerId)
    }

    Scaffold(
        topBar = { TopBarGoBack("Vendedor", navController) },
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

                    isSeller == null -> {
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
                        InfoSeller(isSeller as SellerModel)
                    }
                }
            }
        },
    )
}

@Composable
private fun InfoSeller(seller: SellerModel) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 16.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (seller.photo != null) {
            AsyncImage(
                model = seller.photo,
                contentDescription = "img_seller",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(96.dp),
                placeholder = painterResource(R.drawable.default_user)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = "img_seller",
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
            InformationField("Nombre", seller.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Apellido", seller.lastName)
            Spacer(Modifier.height(24.dp))
            InformationField("Correo electrónico", seller.email ?: "")
            Spacer(Modifier.height(24.dp))
            InformationField(
                "Día de nacimiento",
                formatIsoDateToLocalDate(seller.birthday),
                imageVector = Icons.Filled.DateRange
            )
            Spacer(Modifier.height(24.dp))
            InformationField("Género", formatGender(seller.gender))
            Spacer(Modifier.height(24.dp))
            InformationField("Número de celular", seller.phoneNumber)
        }
    }
}
