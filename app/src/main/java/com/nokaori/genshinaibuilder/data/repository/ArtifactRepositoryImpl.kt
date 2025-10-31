package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.repository.ArtifactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArtifactRepositoryImpl : ArtifactRepository {
    private val _artifacts = MutableStateFlow<List<Artifact>>(emptyList())
    private val _artifactSets = MutableStateFlow<List<ArtifactSet>>(emptyList())

    init {
        _artifactSets.value = listOf(
            ArtifactSet("Киноварное загробье"),
            ArtifactSet("Ночь открытого неба")
        )
    }

    override fun getArtifacts(): Flow<List<Artifact>> = _artifacts.asStateFlow()
    override fun getAvailableArtifactSets(): Flow<List<ArtifactSet>> = _artifactSets.asStateFlow()

    override suspend fun addArtifact(artifact: Artifact) {
        _artifacts.update { currentList ->
            currentList + artifact
        }
    }
}