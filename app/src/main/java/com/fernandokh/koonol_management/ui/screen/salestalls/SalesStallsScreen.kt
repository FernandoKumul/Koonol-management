package com.fernandokh.koonol_management.ui.screen.salestalls

import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.DialogC
import com.fernandokh.koonol_management.ui.components.shared.DropdownMenuC
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
import com.fernandokh.koonol_management.utils.MenuItem
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.viewModel.salesstalls.SalesStallsViewModel

@Composable
fun SalesStallsScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: SalesStallsViewModel = viewModel()
) {
    val salesStalls = viewModel.salesStallsPagingFlow.collectAsLazyPagingItems()
    val totalRecords by viewModel.isTotalRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchSalesStalls()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarMenuTitle("Puestos", drawerState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateSaleStall.route) },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                SearchTopBar(viewModel)

                when {
                    //Carga inicial
                    salesStalls.loadState.refresh is LoadState.Loading && (salesStalls.itemCount == 0 || isLoading) -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    //Estado vacio
                    salesStalls.loadState.refresh is LoadState.NotLoading && salesStalls.itemCount == 0 -> {
                        Text(
                            text = "No se encontraron puestos",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 0.dp)
                        )
                    }

                    //Error
                    salesStalls.loadState.hasError -> {
                        Box(
                            Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Ha ocurrido un error")
                        }
                    }

                    else -> {
                        SalesStallsList(salesStalls, navController, totalRecords, viewModel)
                    }
                }
            }
        },
    )
}

@Composable
private fun SearchTopBar(viewModel: SalesStallsViewModel) {
    val isValueSearch by viewModel.isValueSearch.collectAsState()
    var filtersOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(12.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarC(
            text = isValueSearch,
            placeholder = "Buscar por nombre, tipo o categoría",
            modifier = Modifier.weight(1f),
            onSearch = { viewModel.searchSalesStalls() },
            onChange = { viewModel.changeValueSearch(it) }
        )
        IconButton(
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 0.dp),
            onClick = { filtersOpen = true }
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(R.drawable.baseline_filter_list_alt_24),
                contentDescription = "ic_filter"
            )
        }
    }

    FiltersDialog(
        filtersOpen,
        onDismiss = { filtersOpen = false },
        viewModel
    )
}

@Composable
private fun FiltersDialog(open: Boolean, onDismiss: () -> Unit, viewModel: SalesStallsViewModel) {
    val isSortOption by viewModel.isSortOption.collectAsState()

    if (open) {
        var sortOptionCurrent by remember { mutableStateOf(isSortOption) }
        DialogC(onDismissRequest = { onDismiss() }) {
            Text(
                "Filtros",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            Text("Ordernar por", color = MaterialTheme.colorScheme.primary)
            CustomSelect(
                options = viewModel.optionsSort,
                selectedOption = isSortOption,
                onOptionSelected = { sortOptionCurrent = it }
            )
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    viewModel.changeFilters(sortOptionCurrent)
                    onDismiss()
                }, Modifier.fillMaxWidth(0.8f)) {
                    Text("Aplicar")
                }
            }
        }
    }
}

@Composable
private fun SalesStallsList(
    salesstalls: LazyPagingItems<SalesStallsModel>,
    navController: NavHostController,
    total: Int,
    viewModel: SalesStallsViewModel
) {
    val isSalesStallsToDelete by viewModel.isSalesStallsToDelete.collectAsState()
    val isLoadingDelete by viewModel.isLoadingDelete.collectAsState()

    if (isSalesStallsToDelete != null) {
        AlertDialogC(
            dialogTitle = "Borrar Puesto",
            dialogText = "¿Estás seguro de borrar el puesto ${isSalesStallsToDelete?.name}?",
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = { viewModel.deleteSalesStalls() },
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
        items(salesstalls.itemCount) {
            salesstalls[it]?.let { salesstall ->
                CardSalesStallItem(
                    navController,
                    salesstall,
                    options
                ) { viewModel.onSalesStallsSelectedForDelete(salesstall) }
            }
        }

        when {
            salesstalls.loadState.append is LoadState.NotLoading && salesstalls.loadState.append.endOfPaginationReached -> {
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

            salesstalls.loadState.append is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            salesstalls.loadState.append is LoadState.Error -> {
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
private fun CardSalesStallItem(
    navController: NavHostController,
    salesStall: SalesStallsModel,
    options: List<MenuItem>,
    onSelectedToDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        Modifier.padding(16.dp, 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (salesStall.photos != null) {
            AsyncImage(
                model = salesStall.photos[0],
                contentDescription = "img_SalesStalls",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_image),
                contentDescription = "img_SalesStalls",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
            )
        }
        Column(
            Modifier.weight(1f),
        ) {
            Text(
                text = salesStall.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp
            )
            Text(
                text = salesStall.type,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
            Text(
                text = salesStall.subCategoryId.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
        }
        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = { menuOpen = !menuOpen }
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "options")
            DropdownMenuC(
                expanded = menuOpen,
                onDismiss = { menuOpen = false },
                options = options,
                onItemClick = { option ->
                    when (option.name) {
                        "Más información" -> navController.navigate(
                            Screen.InfoSaleStall.createRoute(
                                salesStall.id
                            )
                        )

                        "Editar" -> navController.navigate(
                            Screen.EditSaleStall.createRoute(
                                salesStall.id
                            )
                        )

                        "Borrar" -> onSelectedToDelete()
                    }
                }
            )
        }
    }
    HorizontalDivider(thickness = 1.dp)
}
