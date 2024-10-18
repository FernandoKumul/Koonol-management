package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.AlertDialogC
import com.fernandokh.koonol_management.ui.components.shared.CustomSelect
import com.fernandokh.koonol_management.ui.components.shared.DialogC
import com.fernandokh.koonol_management.ui.components.shared.DropdownMenuC
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.MenuItem
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.viewModel.users.UserViewModel

@Composable
fun UsersScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: UserViewModel = viewModel()
) {
    val users = viewModel.userPagingFlow.collectAsLazyPagingItems()
    val totalRecords by viewModel.isTotalRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchUsers()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetToastMessage()
        }
    }

    Scaffold(
        topBar = { TopBarMenuTitle("Usuarios", drawerState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.CreateUser.route) },
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
                    users.loadState.refresh is LoadState.Loading && (users.itemCount == 0 || isLoading) -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    //Estado vacio
                    users.loadState.refresh is LoadState.NotLoading && users.itemCount == 0 -> {
                        Text(
                            text = "No se encontraron usuarios",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 0.dp)
                        )
                    }

                    //Error
                    users.loadState.hasError -> {
                        Box(
                            Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Ha ocurrido un error")
                        }
                    }

                    else -> {
                        UsersList(users, navController, totalRecords, viewModel)
                    }
                }
            }
        },
    )
}

@Composable
fun SearchTopBar(viewModel: UserViewModel) {
    val isValueSearch by viewModel.isValueSearch.collectAsState()
    var filtersOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.padding(12.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBarC(
            text = isValueSearch,
            placeholder = "Buscar por nombre, correo",
            modifier = Modifier.weight(1f),
            onSearch = { viewModel.searchUsers() },
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

    FiltersDialog(filtersOpen, onDismiss = { filtersOpen = false }, viewModel)
}

@Composable
fun FiltersDialog(open: Boolean, onDismiss: () -> Unit, viewModel: UserViewModel) {
    val isSortOption by viewModel.isSortOption.collectAsState()
    val isRolFilterOption by viewModel.isRolFilterOption.collectAsState()


    if (open) {
        var sortOptionCurrent by remember { mutableStateOf(isSortOption) }
        var rolOptionCurrent by remember { mutableStateOf(isRolFilterOption) }
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
                onOptionSelected = {sortOptionCurrent = it}
            )
            Spacer(Modifier.height(12.dp))

            Text("Roles", color = MaterialTheme.colorScheme.primary)
            CustomSelect(
                options = viewModel.optionsRol,
                selectedOption = isRolFilterOption,
                onOptionSelected = {rolOptionCurrent = it}
            )
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    viewModel.changeFilters(sortOptionCurrent, rolOptionCurrent)
                    onDismiss()
                }, Modifier.fillMaxWidth(0.8f)) {
                    Text("Aplicar")
                }
            }
        }
    }
}

@Composable
fun UsersList(
    users: LazyPagingItems<UserInModel>,
    navController: NavHostController,
    total: Int,
    viewModel: UserViewModel
) {
    val isUserToDelete by viewModel.isUserToDelete.collectAsState()
    val isLoadingDelete by viewModel.isLoadingDelete.collectAsState()

    if (isUserToDelete != null) {
        AlertDialogC(
            dialogTitle = "Borrar Usuario",
            dialogText = "¿Estás seguro de borrar el usuario ${isUserToDelete?.name} ${isUserToDelete?.lastName}?",
            onDismissRequest = { viewModel.dismissDialog() },
            onConfirmation = { viewModel.deleteUser() },
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
        items(users.itemCount) {
            users[it]?.let { user ->
                CardUserItem(
                    navController,
                    user,
                    options
                ) { viewModel.onUserSelectedForDelete(user) }
            }
        }

        when {
            users.loadState.append is LoadState.NotLoading && users.loadState.append.endOfPaginationReached -> {
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

            users.loadState.append is LoadState.Loading -> {
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

            users.loadState.append is LoadState.Error -> {
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
fun CardUserItem(
    navController: NavHostController,
    user: UserInModel,
    options: List<MenuItem>,
    onSelectedToDelete: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        Modifier.padding(16.dp, 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (user.photo != null) {
            AsyncImage(
                model = user.photo,
                contentDescription = "img_user",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
            )
        } else {
            Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = "img_user",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = "${user.name} ${user.lastName}",
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = user.rol.name,
                    fontSize = 16.sp
                )
            }
            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = user.email,
                fontSize = 14.sp
            )
        }
        IconButton(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically),
            onClick = { menuOpen = true }
        ) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "options")
            DropdownMenuC(
                expanded = menuOpen,
                onDismiss = { menuOpen = false },
                options = options,
                onItemClick = { option ->
                    when (option.name) {
                        "Más información" -> navController.navigate(Screen.InfoUser.createRoute(user.id))
                        "Editar" -> navController.navigate(Screen.EditUser.createRoute(user.id))
                        "Borrar" -> {
                            onSelectedToDelete()
                        }
                    }
                })
        }
    }
    HorizontalDivider(thickness = 1.dp)
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "spec:parent=pixel_5"
)
@Composable
fun PrevUsersScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    KoonolmanagementTheme(dynamicColor = false) {
        UsersScreen(navController, drawerState)
    }
}