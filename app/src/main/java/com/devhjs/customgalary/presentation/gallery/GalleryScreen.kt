package com.devhjs.customgalary.presentation.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.presentation.component.LoadingIndicator
import com.devhjs.customgalary.presentation.component.PhotoItem

@Composable
fun GalleryScreen(
    pagedPhotos: LazyPagingItems<Photo>,
    onPhotoClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
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
                PhotoItem(photo = photo, onClick = { onPhotoClick(photo.id) })
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
            }
        }
    }
}

