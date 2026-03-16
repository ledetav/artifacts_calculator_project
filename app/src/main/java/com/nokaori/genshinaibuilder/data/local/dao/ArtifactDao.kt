package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource

@Dao
interface ArtifactDao {
    // --- SETS ---
    @Query("SELECT * FROM artifact_sets_data WHERE language = :language ORDER BY name ASC")
    fun getAllArtifactSets(language: String): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data WHERE language = :language ORDER BY name ASC")
    fun getAllArtifactSetsPaging(language: String): PagingSource<Int, ArtifactSetEntity>

    @Query("SELECT icon_url FROM artifact_sets_data WHERE language = :language UNION SELECT icon_url FROM artifact_pieces_data WHERE language = :language")
    suspend fun getAllArtifactUrls(language: String): List<String>

    @Query("SELECT * FROM artifact_sets_data WHERE language = :language AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtifactSets(language: String, query: String): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data WHERE id = :setId AND language = :language")
    suspend fun getArtifactSetById(setId: Int, language: String): ArtifactSetEntity?

    @Query("SELECT DISTINCT id FROM artifact_sets_data")
    suspend fun getAllArtifactSetIds(): List<Int>

    @Query("SELECT * FROM artifact_sets_data WHERE language = :language AND name = :name LIMIT 1")
    suspend fun getSetByName(language: String, name: String): ArtifactSetEntity?

    @Upsert
    suspend fun insertArtifactSets(sets: List<ArtifactSetEntity>)

    // --- PIECES ---
    @Query("SELECT * FROM artifact_pieces_data WHERE set_id = :setId AND language = :language")
    fun getPiecesBySetId(setId: Int, language: String): Flow<List<ArtifactPieceEntity>>

    @Query("SELECT * FROM artifact_pieces_data")
    suspend fun getAllArtifactPieces(): List<ArtifactPieceEntity>

    @Upsert
    suspend fun insertArtifactPieces(pieces: List<ArtifactPieceEntity>)

    // --- CLEAR CACHE ---
    @Query("DELETE FROM artifact_sets_data WHERE language = :language")
    suspend fun clearArtifactSetsByLanguage(language: String)

    @Query("DELETE FROM artifact_sets_data")
    suspend fun clearAllArtifactSets()
}