package com.devhjs.customgalary.domain.repository

import androidx.paging.PagingData
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.model.PhotoDetail
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    fun getPhotos(): Flow<PagingData<Photo>>
    suspend fun getPhotoDetail(uri: String): PhotoDetail
}
