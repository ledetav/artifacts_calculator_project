package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.usecase.FilterArtifactsUseCase
import com.nokaori.genshinaibuilder.domain.usecase.FilterWeaponsUseCase

class ViewModelFactory(
    private val artifactRepository: ArtifactRepository,
    private val weaponRepository: WeaponRepository,
    private val themeRepository: ThemeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ArtifactViewModel::class.java) -> {
                ArtifactViewModel(
                    artifactRepository = artifactRepository,
                    filterArtifactsUseCase = FilterArtifactsUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(WeaponViewModel::class.java) -> {
                WeaponViewModel(
                    weaponRepository = weaponRepository,
                    filterWeaponsUseCase = FilterWeaponsUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(themeRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}