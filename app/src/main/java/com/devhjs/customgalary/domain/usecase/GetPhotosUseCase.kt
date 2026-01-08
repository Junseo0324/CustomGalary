package com.devhjs.customgalary.domain.usecase

import androidx.paging.PagingData
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val repository: GalleryRepository
) {
    operator fun invoke(): Flow<PagingData<Photo>> {
        return repository.getPhotos()
    }
}
