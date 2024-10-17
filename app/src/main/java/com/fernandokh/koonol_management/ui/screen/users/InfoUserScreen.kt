package com.fernandokh.koonol_management.ui.screen.users

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.ui.components.router.TopBarGoBack
import com.fernandokh.koonol_management.ui.theme.KoonolmanagementTheme
import com.fernandokh.koonol_management.utils.formatIsoDateToLocalDate

@Composable
fun InfoUserScreen(navController: NavHostController, userId: String?) {
    Scaffold(
        topBar = { TopBarGoBack("Usuario", navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = "",
                    contentDescription = "img_user",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(96.dp),
                    placeholder = painterResource(R.drawable.default_user)
                )
                Spacer(modifier = Modifier.height(20.dp))
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
                    InformationField("Rol", "Administrador")
                    Spacer(Modifier.height(24.dp))
                    InformationField("Nombre", "John")
                    Spacer(Modifier.height(24.dp))
                    InformationField("Apellido", "Doe")
                    Spacer(Modifier.height(24.dp))
                    InformationField(
                        "Día de nacimiento",
                        formatIsoDateToLocalDate("1990-05-10T00:00:00.000+00:00"),
                        imageVector =  Icons.Filled.DateRange
                    )
                    Spacer(Modifier.height(24.dp))
                    InformationField("Género", "Masculino")
                    Spacer(Modifier.height(24.dp))
                    InformationField("Número de celular", "999 123 45678")
                }
            }
        },
    )
}

@Composable
fun InformationField(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null
) {
    Column(modifier) {
        Text(
            title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text, modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 4.dp).weight(1f))
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "icon",
                    tint = MaterialTheme.colorScheme.outlineVariant

                )
            }
        }
        HorizontalDivider()
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PrevInfoUserScreen() {
    val navController = rememberNavController()
    KoonolmanagementTheme(dynamicColor = false) {
        InfoUserScreen(navController, "Id")
    }
}