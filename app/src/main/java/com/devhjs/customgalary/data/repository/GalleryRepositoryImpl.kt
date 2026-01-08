package com.devhjs.customgalary.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.devhjs.customgalary.data.source.PhotoDataSource
import com.devhjs.customgalary.data.source.PhotoPagingSource
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.model.PhotoDetail
import com.devhjs.customgalary.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    private val dataSource: PhotoDataSource
) : GalleryRepository {

    override fun getPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(dataSource) }
        ).flow
    }

    override suspend fun getPhotoDetail(uri: String): PhotoDetail {
        return dataSource.getPhotoDetail(uri)
    }
}
