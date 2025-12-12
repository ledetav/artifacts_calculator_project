package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nokaori.genshinaibuilder.GenshinBuilderApplication
import com.nokaori.genshinaibuilder.domain.usecase.*

class ViewModelFactory(
    private val application: GenshinBuilderApplication
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Достаем готовый контейнер из Application
        val container = application.container
        
        return when {
            modelClass.isAssignableFrom(ArtifactViewModel::class.java) -> {
                ArtifactViewModel(
                    artifactRepository = container.artifactRepository,
                    filterArtifactsUseCase = FilterArtifactsUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(WeaponViewModel::class.java) -> {
                WeaponViewModel(
                    weaponRepository = container.weaponRepository,
                    filterWeaponsUseCase = FilterWeaponsUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(CharacterViewModel::class.java) -> {
                CharacterViewModel(
                    characterRepository = container.characterRepository,
                    filterCharactersUseCase = FilterCharactersUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(container.themeRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    updateGameDataUseCase = UpdateGameDataUseCase(container.gameDataRepository)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) 
                
                return ViewModelFactory(application as GenshinBuilderApplication).create(modelClass)
            }
        }
    }
}