package com.fernandokh.koonol_management.ui.components.router

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun RouteListMenu(routes: List<RouteItem>, navController: NavHostController, drawerState: DrawerState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val scope = rememberCoroutineScope()

        routes.forEach { route ->
            Row(
                modifier = Modifier
                    .clickable {
                        navController.navigate(route.routeName)
                        scope.launch {
                            drawerState.apply {
                                close()
                            }
                        }
                        navController.navigate(route.routeName)
                    }
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = route.icon,
                    contentDescription = route.routeName,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = route.text)
            }
        }
    }
}