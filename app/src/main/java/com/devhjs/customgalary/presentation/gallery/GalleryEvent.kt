package com.devhjs.customgalary.presentation.gallery

// Event: 네비게이션, 토스트 메시지 등 단발성 이벤트를 정의합니다.
sealed interface GalleryEvent {
    data class NavigateToDetail(val photoId: Long) : GalleryEvent
    data class ShareImage(val imageUri: String) : GalleryEvent
    data class ShowToast(val message: String) : GalleryEvent
}
