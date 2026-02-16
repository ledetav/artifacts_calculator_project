package com.nokaori.genshinaibuilder.domain.repository

import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import androidx.paging.PagingData
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.StatType
import kotlinx.coroutines.flow.Flow

interface ArtifactRepository {
    fun getArtifacts(): Flow<List<Artifact>>
    fun getAvailableArtifactSetsPaged(): Flow<PagingData<ArtifactSet>>
    fun getAvailableArtifactSets(): Flow<List<ArtifactSet>>
    suspend fun addArtifact(artifact: Artifact)
    suspend fun getAllArtifactUrls(): List<String>
    suspend fun getArtifactSetDetails(setId: Int): ArtifactSet
    suspend fun getArtifactMainStatCurve(rarity: Int, statType: StatType): StatCurve?
    suspend fun getArtifactSubStatRolls(rarity: Int, statType: StatType): List<Float>?
}