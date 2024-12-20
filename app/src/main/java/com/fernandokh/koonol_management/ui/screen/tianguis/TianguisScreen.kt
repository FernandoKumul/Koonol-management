package com.fernandokh.koonol_management.ui.screen.tianguis

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
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
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.DropdownMenuC
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.viewModel.tianguis.TianguisViewModel

@Composable
fun TianguisScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: TianguisViewModel = viewModel()
) {
    val tianguisItems = viewModel.tianguisPagingFlow.collectAsLazyPagingItems()
    val totalRecords by viewModel.isTotalRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchTianguis()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    // Debugging: Verificar el total de registros y la cantidad de ítems mostrados
    Log.d("TianguisScreen", "Total Records: $totalRecords")
    Log.d("TianguisScreen", "Items Shown: ${tianguisItems.itemCount}")

    Scaffold(
        topBar = { TopBarMenuTitle("Tianguis", drawerState) },
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateTianguis.route) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Tianguis")
                }
                Spacer(Modifier.width(14.dp))
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.CreateScheduleTianguis.route) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_edit_calendar_24),
                        contentDescription = "Agregar Horario"
                    )
                }
            }
        },
        content = { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                SearchTopBar(viewModel)

                when {
                    tianguisItems.loadState.refresh is LoadState.Loading && (tianguisItems.itemCount == 0 || isLoading) -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    tianguisItems.loadState.refresh is LoadState.NotLoading && tianguisItems.itemCount == 0 -> {
                        Text(
                            text = "No se encontraron tianguis",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    tianguisItems.loadState.hasError -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Ha ocurrido un error al cargar los datos")
                        }
                    }

                    else -> {
                        TianguisList(tianguisItems, navController, totalRecords, viewModel)
                    }
                }
            }
        }
    )
}


@Composable
private fun SearchTopBar(viewModel: TianguisViewModel) {
    val isValueSearch by viewModel.isValueSearch.collectAsState()

    Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarC(
            text = isValueSearch,
            placeholder = "Buscar por nombre o ubicación",
            modifier = Modifier.weight(1f),
            onSearch = { viewModel.searchTianguis() },
            onChange = { viewModel.changeValueSearch(it) }
        )
    }
}

@Composable
private fun TianguisList(
    tianguisItems: LazyPagingItems<TianguisModel>,
    navController: NavHostController,
    total: Int,
    viewModel: TianguisViewModel
) {
    val isTianguisToDelete by viewModel.isTianguisToDelete.collectAsState()
    val isLoadingDelete by viewModel.isLoadingDelete.collectAsState()

    if (isTianguisToDelete != null) {
        AlertDialogC(
            dialogTitle = "Borrar Tianguis",
            dialogText = "¿Estás seguro de borrar el tianguis ${isTianguisToDelete?.name}?",
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = { viewModel.deleteTianguis() },
            loading = isLoadingDelete
        )
    }

    Text(
        text = "Resultados: $total",
        textAlign = TextAlign.End,
        modifier = Modifier.padding(12.dp)
    )

    LazyColumn {
        items(tianguisItems.itemCount) { index ->
            tianguisItems[index]?.let { tianguis ->
                CardTianguisItem(
                    navController = navController,
                    tianguis = tianguis,
                    onSelectedToDelete = { viewModel.onTianguisSelectedForDelete(tianguis) }
                )
            }
        }

        when {
            tianguisItems.loadState.append is LoadState.NotLoading && tianguisItems.loadState.append.endOfPaginationReached && tianguisItems.itemCount > 7 -> {
                item {
                    Text(
                        text = "Has llegado al final de la lista",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            tianguisItems.loadState.append is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            tianguisItems.loadState.append is LoadState.Error -> {
                val error = (tianguisItems.loadState.append as LoadState.Error).error
                Log.e("TianguisList", "Error al cargar más datos: ${error.localizedMessage}")
                item {
                    Text(
                        text = "Error al cargar más datos: ${error.localizedMessage}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
private fun CardTianguisItem(
    navController: NavHostController,
    tianguis: TianguisModel,
    onSelectedToDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
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

    Row(Modifier.padding(16.dp)) {
        Column(Modifier.weight(1f)) {
            Text(text = tianguis.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = tianguis.locality ?: "Ubicación no disponible")
        }
        IconButton(
            onClick = { menuOpen = true },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Opciones")
            DropdownMenuC(
                expanded = menuOpen,
                onDismiss = { menuOpen = false },
                options = options,
                onItemClick = { option ->
                    when (option.name) {
                        "Más información" -> navController.navigate(
                            Screen.InfoTianguis.createRoute(
                                tianguis.id
                            )
                        )

                        "Editar" -> navController.navigate(Screen.EditTianguis.createRoute(tianguis.id))
                        "Borrar" -> onSelectedToDelete()
                    }
                }
            )
        }
    }
    Divider()
}

