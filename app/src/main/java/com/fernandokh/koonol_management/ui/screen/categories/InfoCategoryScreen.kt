package com.fernandokh.koonol_management.ui.screen.categories

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.data.models.CategoryWithSubModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.components.shared.InformationField
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.viewModel.categories.InfoCategoryViewModel

@Composable
fun InfoCategoryScreen(
    navController: NavHostController,
    categoryId: String?,
    tokenManager: TokenManager,
    viewModel: InfoCategoryViewModel = viewModel()
) {


    val isCategory by viewModel.isCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.getUser(token, categoryId ?: "")
        }
    }

    Scaffold(
        topBar = { TopBarGoBack("Categoría", navController) },
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

                    isCategory == null -> {
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
                        InfoCategory(isCategory!!)
                    }
                }
            }
        },
    )
}

@Composable
private fun InfoCategory(category: CategoryWithSubModel) {
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
            InformationField("Categoría", category.name)
            Spacer(Modifier.height(24.dp))
            InformationField("Tarifa recomendada", category.recommendedRate.toString())
        }
        Spacer(Modifier.height(20.dp))
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
            if (category.subcategories.isEmpty()) {
                Text("No tiene ninguna categoría")
            } else {
                Column (
                    Modifier.wrapContentHeight()
                ) {
                    category.subcategories.forEachIndexed { index, subcategory ->
                        InformationField("", subcategory.name)
                        if (index != category.subcategories.size - 1) {
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
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        InfoCategoryScreen(navController, "2", TokenManager(LocalContext.current))
    }
}