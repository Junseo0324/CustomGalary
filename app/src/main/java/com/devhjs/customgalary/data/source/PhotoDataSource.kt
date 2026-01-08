package com.devhjs.customgalary.data.source

import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.model.PhotoDetail

interface PhotoDataSource {
    suspend fun getPhotos(limit: Int, offset: Int): List<Photo>
    suspend fun getPhotoDetail(uri: String): PhotoDetail
}
