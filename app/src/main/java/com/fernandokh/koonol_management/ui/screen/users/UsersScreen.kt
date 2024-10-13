package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.Screen
import com.fernandokh.koonol_management.ui.components.router.TopBarMenuTitle
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option

@Composable
fun UsersScreen(navController: NavHostController, drawerState: DrawerState) {
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
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                item { CardUserItem(navController) }
            }
        },
    )
}

@Composable
fun CardUserItem(navController: NavHostController) {
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
                    text = "Jonh Doe Default",
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
                    text = "Rol",
                    fontSize = 16.sp
                )
            }
            Text(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = "defaultemail@gmail.com",
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