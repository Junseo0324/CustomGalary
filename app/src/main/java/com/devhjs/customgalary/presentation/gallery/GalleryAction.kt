package com.devhjs.customgalary.presentation.gallery

// Action: 사용자의 행동이나 이벤트를 정의합니다.
sealed interface GalleryAction {
    data object CheckPermission : GalleryAction
    data class PermissionResult(val isGranted: Boolean) : GalleryAction
    data class OnPhotoLongClick(val photoId: Long) : GalleryAction
}
