package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.paging.PagingData
import androidx.paging.cachedIn

class EncyclopediaViewModel(
    artifactRepository: ArtifactRepository,
    weaponRepository: WeaponRepository
) : ViewModel() {

    val artifactSetsPaged: Flow<PagingData<ArtifactSet>> = artifactRepository.getAvailableArtifactSetsPaged()
        .cachedIn(viewModelScope)
    
    val weaponsPaged: Flow<PagingData<Weapon>> = weaponRepository.getAllWeaponsPaged()
        .cachedIn(viewModelScope)
}