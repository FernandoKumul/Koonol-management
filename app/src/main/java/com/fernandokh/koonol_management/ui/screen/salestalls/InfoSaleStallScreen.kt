package com.fernandokh.koonol_management.ui.screen.salestalls

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.viewModel.salesstalls.InfoSaleStallViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue

@Composable
fun InfoSaleStallScreen(
    navController: NavHostController,
    saleStallId: String?,
    viewModel: InfoSaleStallViewModel = viewModel()
) {
    val isSaleStall by viewModel.isSaleStall.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSaleStall(saleStallId)
    }

    Scaffold(
        topBar = { TopBarGoBack("Puesto", navController) },
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

                    isSaleStall == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontró el puesto",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    else -> {
                        InfoSaleStall(isSaleStall as SalesStallsModel)
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun InfoSaleStall(saleStall: SalesStallsModel) {
    val pagerState = rememberPagerState(initialPage = 0)
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(2600)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % (pagerState.pageCount)
            )
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(0.dp, 16.dp, 0.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (saleStall.photos != null) {
            HorizontalPager(
                count = saleStall.photos.size,
                state = pagerState,
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth()
            ) { page ->
                Card(
                    modifier = Modifier
                        .graphicsLayer {
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                ) {
                    AsyncImage(
                        model = saleStall.photos[page],
                        contentDescription = "img_sale_stall",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(300.dp),
                        placeholder = painterResource(R.drawable.default_image)
                    )
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.outlineVariant,
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_image),
                contentDescription = "img_seller",
                modifier = Modifier.height(250.dp).width(300.dp)
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
            InformationField("Nombre", saleStall.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Vendedor", "${saleStall.sellerId.name} ${saleStall.sellerId.lastName}")
            Spacer(Modifier.height(24.dp))
            InformationField("SubCategoría", saleStall.subCategoryId.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Tipo de puesto", saleStall.type)
            Spacer(Modifier.height(24.dp))
            InformationField("Periodo de prueba", if (saleStall.probation) "Sí" else "No")
            Spacer(Modifier.height(24.dp))
            InformationField("Estado", if (saleStall.active) "Activo" else "Inactivo")
            Spacer(Modifier.height(24.dp))
            InformationField("Descripción", saleStall.description)
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun InfoSaleStallScreenPreview() {
    KoonolmanagementTheme(dynamicColor = false) {
        InfoSaleStallScreen(navController = rememberNavController(), saleStallId = 1.toString())
    }
}