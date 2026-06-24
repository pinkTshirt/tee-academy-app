package com.teeacademy.app.ui.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teeacademy.app.domain.model.Video
import com.teeacademy.app.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val videoId: String = savedStateHandle.get<String>("videoId") ?: ""

    val video: StateFlow<Video?> = videoRepository.getVideoById(videoId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
