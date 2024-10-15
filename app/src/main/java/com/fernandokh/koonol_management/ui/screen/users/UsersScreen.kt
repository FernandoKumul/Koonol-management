package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.data.models.UserInModel
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.components.shared.SearchBarC
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.viewModel.UserViewModel

@Composable
fun UsersScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: UserViewModel = viewModel()
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val totalRecords by viewModel.isTotalRecords.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchUsers()
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
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    UsersList(users, navController, totalRecords)
                }
            }
        },
    )
}

@Composable
fun SearchTopBar(viewModel: UserViewModel) {
    val isValueSearch by viewModel.isValueSearch.collectAsState()

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
            onClick = {}
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(R.drawable.baseline_filter_list_alt_24),
                contentDescription = "ic_filter"
            )
        }
    }
}

@Composable
fun UsersList(users: List<UserInModel>, navController: NavHostController, total: Int) {
    Text(
        text = "Resultados: $total",
        textAlign = TextAlign.End,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(12.dp, 8.dp, 12.dp, 0.dp)
    )
    LazyColumn {
        items(users) { user ->
            CardUserItem(navController, user)
        }
    }
}

@Composable
fun CardUserItem(navController: NavHostController, user: UserInModel) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        Modifier.padding(16.dp, 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.default_user),
            contentDescription = "img_user",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
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
            UserMenu(
                expanded = menuOpen,
                onDismiss = { menuOpen = false },
                onItemClick = { option ->
                    when (option.name) {
                        "Más información" -> navController.navigate(Screen.InfoUser.route)
                        "Editar" -> navController.navigate(Screen.EditUser.route)
                        "Borrar" -> {/**/
                        }
                    }
                })
        }
    }
    HorizontalDivider(thickness = 1.dp)
}

@Composable
fun UserMenu(expanded: Boolean, onDismiss: () -> Unit, onItemClick: (Option) -> Unit) {
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

    DropdownMenu(
        expanded = expanded,
//        modifier = Modifier.shadow(),
        onDismissRequest = { onDismiss() }
//                Modifier.background(MaterialTheme.colorScheme.background)
    ) {

        options.forEach { option ->
            when (option) {
                is Option -> {
                    DropdownMenuItem(
                        onClick = {
                            onDismiss()
                            onItemClick(option)
                        },
                        text = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        tint = option.color,
                                        imageVector = option.icon,
                                        contentDescription = option.name
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = option.name, color = option.color)
                                }
                            }
                        }
                    )
                }

                Divider -> HorizontalDivider()
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PrevUsersScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    KoonolmanagementTheme(dynamicColor = false) {
        UsersScreen(navController, drawerState)
    }
}