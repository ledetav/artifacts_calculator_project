package com.nokaori.genshinaibuilder.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.WeaponRepository
import com.nokaori.genshinaibuilder.domain.usecase.UpdateGameDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateGameDataUseCase: UpdateGameDataUseCase,
    private val characterRepository: CharacterRepository,
    private val weaponRepository: WeaponRepository,
    private val artifactRepository: ArtifactRepository,
    private val imageLoader: ImageLoader,
    private val appContext: Context
) : ViewModel() {

    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
        data class CachingImages(val count: Int) : UpdateState() 
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun updateDatabase() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            
            val result = updateGameDataUseCase()
            
            result.onSuccess {
                _updateState.value = UpdateState.Success
                prefetchImages() 
            }.onFailure { error ->
                _updateState.value = UpdateState.Error(error.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun prefetchImages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val urls = mutableSetOf<String>()
                urls.addAll(characterRepository.getAllCharacterUrls())
                urls.addAll(weaponRepository.getAllWeaponUrls())
                urls.addAll(artifactRepository.getAllArtifactUrls())

                _updateState.value = UpdateState.CachingImages(urls.size)
                Log.d("ImagePrefetch", "Starting prefetch for ${urls.size} images...")

                urls.forEach { url ->
                    val request = ImageRequest.Builder(appContext)
                        .data(url)
                        .memoryCachePolicy(CachePolicy.DISABLED) 
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build()
                    
                    imageLoader.enqueue(request)
                }
            } catch (e: Exception) {
                Log.e("ImagePrefetch", "Error collecting URLs", e)
            }
        }
    }
}