package com.fernandokh.koonol_management.ui.components.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarC (
    text: String,
    placeholder: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: () -> Unit,
) {
    Row (modifier = modifier) {
        androidx.compose.material3.SearchBar(
            query = text,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            onQueryChange = { onChange(it) },
            onSearch = { onSearch() },
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = placeholder,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            active = false,
            content = {},
            onActiveChange = {},
            windowInsets = WindowInsets(top = 0.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
            },
        )
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun PrevSearchBarC() {
    var name by remember { mutableStateOf("") }
    KoonolmanagementTheme {
        SearchBarC(
         name, "Buscar", {name = it}, Modifier, {}
        )
    }
}