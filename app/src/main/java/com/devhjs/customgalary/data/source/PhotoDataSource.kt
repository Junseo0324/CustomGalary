package com.devhjs.customgalary.data.source

import com.devhjs.customgalary.domain.model.Photo

interface PhotoDataSource {
    suspend fun getPhotos(limit: Int, offset: Int): List<Photo>
}
