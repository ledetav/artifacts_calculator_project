package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtifactSetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ArtifactRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val setId: Int = checkNotNull(savedStateHandle["setId"])

    val details: StateFlow<ArtifactSet?> = themeRepository.appLanguage
        .flatMapLatest { _ ->
            repository.getArtifactSetDetailsFlow(setId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}