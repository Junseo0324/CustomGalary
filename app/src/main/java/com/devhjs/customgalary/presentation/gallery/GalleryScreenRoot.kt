package com.devhjs.customgalary.presentation.gallery

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreenRoot(
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedPhotos = viewModel.pagedPhotos.collectAsLazyPagingItems()
    val context = LocalContext.current

    // 권한 요청 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onAction(GalleryAction.PermissionResult(isGranted))
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.selectedPhotoDetail) {
        if (uiState.selectedPhotoDetail != null) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }
        }
    }

    // 초기 권한 확인 및 Event 수집
    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)

        viewModel.event.collectLatest { event ->
            when (event) {
                is GalleryEvent.NavigateToDetail -> {
                    // TODO: 상세 화면 이동 처리
                }
                is GalleryEvent.ShareImage -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/*"
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(event.imageUri))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                }
                is GalleryEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Gallery", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { innerPadding ->
        if (uiState.permissionGranted) {
            GalleryScreen(
                pagedPhotos = pagedPhotos,
                onPhotoClick = { photo -> viewModel.onAction(GalleryAction.OnPhotoClick(photo)) },
                onPhotoLongClick = { photo -> viewModel.onAction(GalleryAction.OnPhotoLongClick(photo)) },
                modifier = Modifier.padding(innerPadding)
            )

            // Bottom Sheet
            if (uiState.selectedPhotoDetail != null) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onAction(GalleryAction.OnDismissBottomSheet) },
                    sheetState = sheetState
                ) {
                    val detail = uiState.selectedPhotoDetail!!
                    PhotoDetailSheetContent(
                        detail = detail,
                        onShareClick = {
                           viewModel.onAction(GalleryAction.OnShareClick(detail.uri))
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "권한이 필요합니다.")
            }
        }
    }
}

@Composable
fun PhotoDetailSheetContent(
    detail: com.devhjs.customgalary.domain.model.PhotoDetail,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Photo Details", style = MaterialTheme.typography.titleLarge)

        DetailItem("Date", detail.dateTaken)
        DetailItem("Time", detail.time)
        DetailItem("File Name", detail.filename)
        DetailItem("Size", detail.fileSize)
        DetailItem("Resolution", detail.resolution)
        if (detail.location != null) {
            DetailItem("Location", detail.location)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share Photo")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
