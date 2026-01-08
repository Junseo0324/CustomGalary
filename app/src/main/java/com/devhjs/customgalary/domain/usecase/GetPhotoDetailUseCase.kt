package com.devhjs.customgalary.domain.usecase

import com.devhjs.customgalary.domain.model.PhotoDetail
import com.devhjs.customgalary.domain.repository.GalleryRepository
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val repository: GalleryRepository
) {
    suspend operator fun invoke(uri: String): PhotoDetail {
        return repository.getPhotoDetail(uri)
    }
}
