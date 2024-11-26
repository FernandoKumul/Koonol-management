package com.fernandokh.koonol_management.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.PromotionSearchModel
import com.fernandokh.koonol_management.data.repository.TokenManager
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomDateField
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.CustomTextField
import com.fernandokh.koonol_management.ui.components.shared.DialogC
import com.fernandokh.koonol_management.ui.components.shared.DropdownMenuC
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
import com.fernandokh.koonol_management.utils.MenuItem
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate
import com.fernandokh.koonol_management.viewModel.promotions.PromotionsViewModel
import com.fernandokh.koonol_management.viewModel.promotions.PromotionsViewModelFactory

@Composable
fun PromotionsScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    tokenManager: TokenManager,
    viewModel: PromotionsViewModel = viewModel(
        factory = PromotionsViewModelFactory(tokenManager)
    )
) {
    val promotions = viewModel.promotionPagingFlow.collectAsLazyPagingItems()
    val totalRecords by viewModel.isTotalRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        tokenManager.accessToken.collect { token ->
            viewModel.searchPromotions(token)
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarMenuTitle("Promociones", drawerState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreatePromotion.route) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                SearchTopBar(viewModel)

                when {
                    //Carga inicial
                    (promotions.loadState.refresh is LoadState.Loading || isLoading) && promotions.itemCount == 0 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    //Estado vacio
                    promotions.loadState.refresh is LoadState.NotLoading && promotions.itemCount == 0 -> {
                        Text(
                            text = "No se encontraron promociones",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 0.dp)
                        )
                    }

                    //Error
                    promotions.loadState.hasError -> {
                        Box(
                            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Ha ocurrido un error")
                        }
                    }

                    else -> {
                        PromotionList(promotions, navController, totalRecords, viewModel)
                    }
                }
            }
        },
    )
}

@Composable
private fun SearchTopBar(viewModel: PromotionsViewModel) {
    val isValueSearch by viewModel.isValueSearch.collectAsState()
    var filtersOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(12.dp, 0.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarC(text = isValueSearch,
            placeholder = "Buscar por nombre",
            modifier = Modifier.weight(1f),
            onSearch = { viewModel.searchPromotions() },
            onChange = { viewModel.changeValueSearch(it) })
        IconButton(modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
            onClick = { filtersOpen = true }) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(R.drawable.baseline_filter_list_alt_24),
                contentDescription = "ic_filter"
            )
        }
    }

    FiltersDialog(filtersOpen, onDismiss = { filtersOpen = false }, viewModel)
}

@Composable
private fun FiltersDialog(open: Boolean, onDismiss: () -> Unit, viewModel: PromotionsViewModel) {
    val isSortOption by viewModel.isSortOption.collectAsState()
    val isMaxPay by viewModel.isMaxPay.collectAsState()
    val isMinPay by viewModel.isMinPay.collectAsState()
    val isStartDate by viewModel.isStartDate.collectAsState()
    val isEndDate by viewModel.isEndDate.collectAsState()

    if (open) {
        var sortOptionCurrent by remember { mutableStateOf(isSortOption) }
        var maxPayCurrent by remember { mutableStateOf(if (isMaxPay == null) "" else isMaxPay.toString()) }
        var minPayCurrent by remember { mutableStateOf(if (isMinPay == null) "" else isMinPay.toString()) }
        var startDate by remember { mutableStateOf(isStartDate) }
        var endDate by remember { mutableStateOf(isEndDate) }

        var maxPayError by remember { mutableStateOf(false) }
        var minPayError by remember { mutableStateOf(false) }

        DialogC(onDismissRequest = { onDismiss() }) {
            Text(
                "Filtros",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(10.dp))

            Text("Ordernar por", color = MaterialTheme.colorScheme.onPrimaryContainer)
            CustomSelect(options = viewModel.optionsSort,
                selectedOption = sortOptionCurrent,
                onOptionSelected = { sortOptionCurrent = it })
            Spacer(Modifier.height(10.dp))

            Text("Pago mínimo", color = MaterialTheme.colorScheme.onPrimaryContainer)
            CustomTextField(
                minPayCurrent,
                {
                    minPayCurrent = it
                    minPayError = !viewModel.validateStringToDouble(it)
                },
                "$0.00",
                keyboardType = KeyboardType.Phone,
                error = minPayError,
                errorMessage = "Número no válido"
            )
            Spacer(Modifier.height(10.dp))

            Text("Pago máximo", color = MaterialTheme.colorScheme.onPrimaryContainer)
            CustomTextField(
                maxPayCurrent,
                {
                    maxPayCurrent = it
                    maxPayError = !viewModel.validateStringToDouble(it)
                },
                "$0.00",
                keyboardType = KeyboardType.Phone,
                error = maxPayError,
                errorMessage = "Número no válido"
            )
            Spacer(Modifier.height(10.dp))

            Text("Fecha de inicio", color = MaterialTheme.colorScheme.onPrimaryContainer)
            CustomDateField(
                { startDate = it },
                startDate
            )
            Spacer(Modifier.height(10.dp))

            Text("Fecha de fin", color = MaterialTheme.colorScheme.onPrimaryContainer)
            CustomDateField(
                {
                    endDate = it
                },
                endDate
            )
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        sortOptionCurrent = viewModel.optionsSort[0]
                        minPayCurrent = ""
                        maxPayCurrent = ""
                        startDate = null
                        endDate = null
                    }
                ) {
                    Text("Restablecer", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (minPayError || maxPayError) {
                            viewModel.showToast("Error al validar")
                            return@Button
                        }
                        viewModel.changeFilters(
                            sortOptionCurrent,
                            minPayCurrent,
                            maxPayCurrent,
                            startDate,
                            endDate
                        )
                        onDismiss()
                    }) {
                    Text("Aplicar")
                }
            }
        }
    }
}

@Composable
private fun PromotionList(
    promotion: LazyPagingItems<PromotionSearchModel>,
    navController: NavHostController,
    total: Int,
    viewModel: PromotionsViewModel
) {
    val isPromotionToDelete by viewModel.isPromotionToDelete.collectAsState()
    val isLoadingDelete by viewModel.isLoadingDelete.collectAsState()

    if (isPromotionToDelete != null) {
        AlertDialogC(
            dialogTitle = "Borrar Promoción",
            dialogText = "¿Estás seguro de borrar la promoción de $${isPromotionToDelete?.pay} asociado al puesto ${isPromotionToDelete?.salesStall?.name}?",
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = { viewModel.deletePromotion() },
            loading = isLoadingDelete
        )
    }

    val options = listOf(
        Option(
            "Más información",
            ImageVector.vectorResource(R.drawable.ic_article_line),
            MaterialTheme.colorScheme.onBackground
        ),
        Divider,
        Option(
            "Editar",
            ImageVector.vectorResource(R.drawable.ic_edit_2_line),
            MaterialTheme.colorScheme.primary
        ),
        Divider,
        Option(
            "Borrar",
            ImageVector.vectorResource(R.drawable.ic_delete_bin_line),
            MaterialTheme.colorScheme.error
        ),
    )

    Text(
        text = "Resultados: $total",
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 0.dp)
    )

    LazyColumn {
        items(promotion.itemCount) {
            promotion[it]?.let { promotion ->
                CardCategoryItem(
                    navController, promotion, options
                ) { viewModel.onPromotionSelectedForDelete(promotion) }
            }
        }

        when {
            promotion.loadState.append is LoadState.NotLoading && promotion.loadState.append.endOfPaginationReached -> {
                if (promotion.itemCount >= 7) {
                    item {
                        Text(
                            text = "Has llegado al final de la lista",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }

            promotion.loadState.append is LoadState.Loading -> {
                // Loader al final de la lista
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            promotion.loadState.append is LoadState.Error -> {
                // En caso de error al cargar más datos
                item {
                    Text(
                        text = "Error al cargar más datos",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 12.dp),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CardCategoryItem(
    navController: NavHostController,
    promotion: PromotionSearchModel,
    options: List<MenuItem>,
    onSelectedToDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        Modifier.padding(24.dp, 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                text = promotion.salesStall.name,
                fontSize = 17.sp
            )
            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = "${formatIsoDateToLocalDate(promotion.startDate)}, ${
                    formatIsoDateToLocalDate(
                        promotion.endDate
                    )
                }",
                fontSize = 15.sp,
                modifier = Modifier.wrapContentWidth(),
                maxLines = 1
            )
            Box(
                Modifier
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(6.dp, 2.dp)
            ) {
                Text(
                    text = "$${promotion.pay}", fontSize = 14.sp
                )
            }
        }
        IconButton(modifier = Modifier.size(32.dp), onClick = { menuOpen = true }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "options")
            DropdownMenuC(expanded = menuOpen,
                onDismiss = { menuOpen = false },
                options = options,
                onItemClick = { option ->
                    when (option.name) {
                        "Más información" -> navController.navigate(
                            Screen.InfoPromotion.createRoute(
                                promotion.id
                            )
                        )

                        "Editar" -> navController.navigate(
                            Screen.EditPromotion.createRoute(
                                promotion.id
                            )
                        )

                        "Borrar" -> {
                            onSelectedToDelete()
                        }
                    }
                })
        }
    }
    HorizontalDivider(thickness = 1.dp)
}
