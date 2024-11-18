package com.fernandokh.koonol_management.ui.components.shared

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.fernandokh.koonol_management.R
import com.fernandokh.koonol_management.utils.MenuItem.Divider
import com.fernandokh.koonol_management.utils.MenuItem.Option
import com.fernandokh.koonol_management.viewModel.MyUploadImageViewModel
import java.io.File

@Composable
fun UploadImage(
    url: String? = null, //target url to preview
    directory: File? = null, // stored directory
    onSetImage: (String?) -> Unit = {}, // selected / taken uri
    viewModel: MyUploadImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val tempUri = remember { mutableStateOf<Uri?>(null) }
    val authority = stringResource(id = R.string.file_provider)
    var openMenu by remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()

    val originalOptions = listOf(
        Option(
            "Tomar foto",
            ImageVector.vectorResource(R.drawable.ic_photo_camera),
            MaterialTheme.colorScheme.onBackground
        ),
        Divider,
        Option(
            "Seleccionar imagen",
            ImageVector.vectorResource(R.drawable.ic_gallery),
            MaterialTheme.colorScheme.onBackground
        ),
        Divider,
        Option(
            "Eliminar",
            ImageVector.vectorResource(R.drawable.ic_delete_bin_line),
            MaterialTheme.colorScheme.error
        ),
    )

    val options = if (url != null) originalOptions else originalOptions.take(3)

    fun getTempUri(): Uri? {
        directory?.let {
            it.mkdirs()
            val file = File(it, "image_avatar.jpg")
            if (file.exists()) {
                file.delete()
            }

            return FileProvider.getUriForFile(
                context,
                authority,
                file.apply { createNewFile() }
            )
        }
        return null
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                viewModel.uploadImage(it, context) { newUrl ->
                    if (newUrl != null) {
                        onSetImage(newUrl)
                    }
                }
            }

        }
    )

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { _ ->
            tempUri.value?.let {
                viewModel.uploadImage(it, context) { newUrl ->
                    if (newUrl != null) {
                        onSetImage(newUrl)
                    }
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Obtener la dirección temporal si tienes persmiso
            val tmpUri = getTempUri()
            tempUri.value = tmpUri
            tempUri.value?.let { takePhotoLauncher.launch(it) }
        } else {
            // Permission is denied, handle it accordingly
        }
    }

    fun takePhotoClick() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val tmpUri = getTempUri()
            tempUri.value = tmpUri
            tempUri.value?.let { takePhotoLauncher.launch(it) }
        } else {
            cameraPermissionLauncher.launch(permission)
        }
    }

    fun photoGalleryClick() {
        imagePicker.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    Box {
        when {
            isLoading -> {
                Box(Modifier.size(96.dp)) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            url != null -> {
                AsyncImage(
                    model = url,
                    contentScale = ContentScale.Crop,
                    contentDescription = "img_captured",
                    modifier = Modifier
                        .height(200.dp)
                        .width(300.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            else -> {
                Image(
                    painter = painterResource(R.drawable.default_image),
                    contentDescription = "img_user",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(200.dp)
                        .width(300.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }

        IconButton(
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !isLoading,
            onClick = { openMenu = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .border(3.dp, MaterialTheme.colorScheme.background, CircleShape)
                .size(40.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_edit_2_line),
                contentDescription = "ic_filter",
                tint = MaterialTheme.colorScheme.onPrimary
            )

            DropdownMenuC(
                expanded = openMenu,
                onDismiss = { openMenu = false },
                options = options,
                onItemClick = { option ->
                    when (option.name) {
                        "Tomar foto" -> takePhotoClick()
                        "Seleccionar imagen" -> photoGalleryClick()
                        "Eliminar" -> {
                            onSetImage(null)
                        }
                    }
                }
            )
        }

    }
}