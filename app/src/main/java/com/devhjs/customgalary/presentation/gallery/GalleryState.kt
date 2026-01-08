package com.devhjs.customgalary.presentation.gallery

// State: UI의 현재 상태를 나타냅니다.
import com.devhjs.customgalary.domain.model.PhotoDetail

data class GalleryState(
    val isLoading: Boolean = false, // 초기 로딩 (Paging LoadState와 별개로 사용 가능)
    val error: String? = null,
    val permissionGranted: Boolean = false,
    val selectedPhotoDetail: PhotoDetail? = null
)
