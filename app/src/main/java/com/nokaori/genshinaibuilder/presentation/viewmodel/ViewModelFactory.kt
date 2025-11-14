package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nokaori.genshinaibuilder.data.repository.ArtifactRepositoryImpl
import com.nokaori.genshinaibuilder.data.repository.WeaponRepositoryImpl
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository

class ViewModelFactory(
    private val artifactRepository: ArtifactRepository,
    private val weaponRepository: WeaponRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ArtifactViewModel::class.java) -> {
                ArtifactViewModel(artifactRepository) as T
            }
            modelClass.isAssignableFrom(WeaponViewModel::class.java) -> {
                WeaponViewModel(weaponRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}