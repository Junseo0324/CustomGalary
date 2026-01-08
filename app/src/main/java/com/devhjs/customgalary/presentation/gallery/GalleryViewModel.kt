package com.devhjs.customgalary.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devhjs.customgalary.domain.model.Photo
import com.devhjs.customgalary.domain.usecase.GetPhotoDetailUseCase
import com.devhjs.customgalary.domain.usecase.GetPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(GalleryState())
    val uiState: StateFlow<GalleryState> = _uiState.asStateFlow()

    // Side Effects
    private val _event = Channel<GalleryEvent>()
    val event = _event.receiveAsFlow()

    // Paging Data Flow
    val pagedPhotos: Flow<PagingData<Photo>> = getPhotosUseCase()
        .cachedIn(viewModelScope)

    fun onAction(action: GalleryAction) {
        when (action) {
            is GalleryAction.CheckPermission -> {

            }
            is GalleryAction.PermissionResult -> {
                _uiState.update { it.copy(permissionGranted = action.isGranted) }
                if (!action.isGranted) {
                    sendEvent(GalleryEvent.ShowToast("권한이 필요합니다."))
                }
            }
            is GalleryAction.OnPhotoClick -> {
                viewModelScope.launch {
                    val detail = getPhotoDetailUseCase(action.photo.uri)
                    _uiState.update { it.copy(selectedPhotoDetail = detail) }
                }
            }
            is GalleryAction.OnPhotoLongClick -> {
                sendEvent(GalleryEvent.ShareImage(action.photo.uri))
            }
            is GalleryAction.OnShareClick -> {
                sendEvent(GalleryEvent.ShareImage(action.uri))
            }
            is GalleryAction.OnDismissBottomSheet -> {
                _uiState.update { it.copy(selectedPhotoDetail = null) }
            }
        }
    }


    private fun sendEvent(event: GalleryEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
