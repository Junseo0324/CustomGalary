package com.devhjs.customgalary.presentation.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.presentation.component.ErrorItem
import com.devhjs.customgalary.presentation.component.LoadingIndicator
import com.devhjs.customgalary.presentation.component.PhotoItem

@Composable
fun GalleryScreen(
    pagedPhotos: LazyPagingItems<Photo>,
    onPhotoClick: (Photo) -> Unit,
    onPhotoLongClick: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (pagedPhotos.itemCount == 0 && pagedPhotos.loadState.refresh is LoadState.NotLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("사진이 없습니다.")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(1.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(pagedPhotos.itemCount) { index ->
                val photo = pagedPhotos[index]
                if (photo != null) {
                    PhotoItem(
                        photo = photo,
                        onClick = { onPhotoClick(photo) },
                        onLongClick = { onPhotoLongClick(photo) }
                    )
                }
            }

            pagedPhotos.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingIndicator() }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { LoadingIndicator() }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage ?: "Unknown Error",
                                onRetry = { retry() }
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = loadState.append as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage ?: "Unknown Error",
                                onRetry = { retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}
