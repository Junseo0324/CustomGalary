package com.devhjs.customgalary.presentation.gallery

import com.devhjs.customgalary.domain.model.Photo

// Action: 사용자의 행동이나 이벤트를 정의합니다.
sealed interface GalleryAction {
    data object CheckPermission : GalleryAction
    data class PermissionResult(val isGranted: Boolean) : GalleryAction
    data class OnPhotoLongClick(val photo: Photo) : GalleryAction
}
