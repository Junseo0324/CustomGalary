package com.devhjs.customgalary.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devhjs.customgalary.domain.model.Photo


@Composable
fun PhotoItem(
    photo: Photo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.aspectRatio(1f) // 정사각형
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.uri)
                .crossfade(true)
                .build(),
            contentDescription = photo.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // 투명 버튼으로 클릭 처리
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.ui.graphics.Color.Transparent
        ) {}
    }
}