package com.devhjs.customgalary.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.devhjs.customgalary.domain.model.Photo
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
    private val getPhotosUseCase: GetPhotosUseCase
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
            is GalleryAction.OnPhotoLongClick -> {
                // PagingData는 Flow이므로 직접 데이터에 접근하기 어렵습니다.
                // 여기서는 간단히 UseCase나 Repository를 통해 ID로 사진을 찾거나,
                // UI에서 클릭 시 URI를 함께 넘겨주는 방법을 고려할 수 있는데
                // 비동기 데이터 흐름상 UI에서 넘겨주는게 가장 간단합니다... 만
                // 현재 구조상 ID만 받고 있습니다.
                // PagingSource로부터 데이터를 가져오는 것은 비용이 듭니다.
                // -> Action을 수정하여 URI를 받거나, UI에서 Photo 객체를 넘기도록 하는 것이 좋겠네요.
                // 일단 간단히 구현하기 위해 Action에 Photo 객체를 넘기지 않고,
                // UI에서 호출 시 처리하도록 구조를 바꿀 수도 있지만,
                // 여기서는 ViewModel에서 PagingData를 직접 조회하기 어려우므로
                // *** Action에 uri String을 추가하는 것으로 변경하겠습니다. ***
                // 하지만 이미 Action 파일은 수정 명령을 내렸으니,
                // 여기 수정과 동시에 Action 파일도 수정해야 합니다. 
                // 아니면... 그냥 viewModelScope.launch 에서 repository.getPhotoById 같은걸 호출해야 하는데
                // UseCase에 그런 기능이 없습니다.
                // 가장 깔끔한건 Action이 Photo 자체나 Uri를 받는 것입니다.
                
                // 이번 턴에서는 일단 주석만 남기고, 다음 턴에 Action을 수정하여 uri를 받도록 하겠습니다.
                // 아, 아직 Action 수정 Tool Call이 Pending 상태가 아니므로
                // 지금 Action을 수정하는 Tool Call을 취소하고 다시 보낼 수 없습니다.
                // 이미 보낸 Tool Call은 실행됩니다.
                
                // => ID로 검색 기능이 없으므로, 잠시 보류.
                // 대신, UI에서 클릭 시 URI를 넘겨주도록 Action을 변경하는 후속 작업을 계획합니다.
            }
        }
    }

    private fun sendEvent(event: GalleryEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
