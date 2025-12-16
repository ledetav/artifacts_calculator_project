package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.nokaori.genshinaibuilder.GenshinBuilderApplication
import com.nokaori.genshinaibuilder.domain.usecase.*
import coil3.SingletonImageLoader

class ViewModelFactory(
    private val application: GenshinBuilderApplication
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
                    gameDataRepository = container.gameDataRepository
                ) as T
            }
            modelClass.isAssignableFrom(EncyclopediaViewModel::class.java) -> {
                EncyclopediaViewModel(
                    artifactRepository = container.artifactRepository,
                    weaponRepository = container.weaponRepository
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