package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EncyclopediaViewModel @Inject constructor (
    artifactRepository: ArtifactRepository,
    weaponRepository: WeaponRepository,
    themeRepository: ThemeRepository
) : ViewModel() {

    val artifactSetsPaged: Flow<PagingData<ArtifactSet>> = themeRepository.appLanguage
        .flatMapLatest { _ ->
            artifactRepository.getAvailableArtifactSetsPaged()
        }
        .cachedIn(viewModelScope)
    
    val weaponsPaged: Flow<PagingData<Weapon>> = themeRepository.appLanguage
        .flatMapLatest { _ ->
            weaponRepository.getAllWeaponsPaged()
        }
        .cachedIn(viewModelScope)
}