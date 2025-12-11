package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.GameDataRepository
import com.nokaori.genshinaibuilder.domain.usecase.FilterArtifactsUseCase
import com.nokaori.genshinaibuilder.domain.usecase.FilterWeaponsUseCase
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.domain.usecase.UpdateGameDataUseCase

class ViewModelFactory(
    private val artifactRepository: ArtifactRepository,
    private val weaponRepository: WeaponRepository,
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository,
    private val gameDataRepository: GameDataRepository
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
            modelClass.isAssignableFrom(CharacterViewModel::class.java) -> {
                CharacterViewModel(
                    characterRepository = characterRepository,
                    filterCharactersUseCase = FilterCharactersUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    updateGameDataUseCase = UpdateGameDataUseCase(gameDataRepository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}