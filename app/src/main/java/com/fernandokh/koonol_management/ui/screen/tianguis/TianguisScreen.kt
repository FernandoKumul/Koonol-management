package com.fernandokh.koonol_management.ui.screen.tianguis

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.DialogC
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
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

    Scaffold(
        topBar = { TopBarMenuTitle("Tianguis", drawerState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { Toast.makeText(context, "Aquí se crearía un nuevo registro", Toast.LENGTH_SHORT).show() },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tianguis")
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
                        TianguisList(tianguisItems, navController, totalRecords)
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
    total: Int
) {
    Text(
        text = "Resultados: $total",
        textAlign = TextAlign.End,
        modifier = Modifier.padding(12.dp)
    )

    LazyColumn {
        items(tianguisItems.itemCount) { index ->
            tianguisItems[index]?.let { tianguis ->
                CardTianguisItem(navController, tianguis)
            }
        }

        // Log para capturar el estado de carga
        Log.d("TianguisScreen", "LoadState: ${tianguisItems.loadState.refresh}")

        when {
            tianguisItems.loadState.append is LoadState.NotLoading && tianguisItems.loadState.append.endOfPaginationReached -> {
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
                    )
                }
            }

            tianguisItems.loadState.append is LoadState.Error -> {
                val error = (tianguisItems.loadState.append as LoadState.Error).error
                Log.e("TianguisScreen", "Error al cargar más datos: ${error.localizedMessage}")
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
private fun CardTianguisItem(navController: NavHostController, tianguis: TianguisModel) {
    Row(Modifier.padding(16.dp)) {
        Column(Modifier.weight(1f)) {
            Text(text = tianguis.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = tianguis.locality ?: "Ubicación no disponible")
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
        }
    }
    Divider()
}
