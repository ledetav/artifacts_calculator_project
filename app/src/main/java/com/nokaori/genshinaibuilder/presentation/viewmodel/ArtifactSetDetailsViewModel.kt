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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtifactSetDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ArtifactRepository,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val setId: Int = checkNotNull(savedStateHandle["setId"])

    private val _details = MutableStateFlow<ArtifactSet?>(null)
    val details: StateFlow<ArtifactSet?> = _details.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            try {
                val set = repository.getArtifactSetDetails(setId)
                _details.value = set
            } catch (e: Exception) {
                _details.value = null
            }
        }
    }
}
