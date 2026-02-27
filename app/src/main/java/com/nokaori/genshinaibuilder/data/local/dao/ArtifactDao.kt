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
    @Query("SELECT * FROM artifact_sets_data ORDER BY name ASC")
    fun getAllArtifactSets(): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data ORDER BY name ASC")
    fun getAllArtifactSetsPaging(): PagingSource<Int, ArtifactSetEntity>

    @Query("SELECT icon_url FROM artifact_sets_data UNION SELECT icon_url FROM artifact_pieces_data")
    suspend fun getAllArtifactUrls(): List<String>

    @Query("SELECT * FROM artifact_sets_data WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtifactSets(query: String): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data WHERE id = :setId")
    suspend fun getArtifactSetById(setId: Int): ArtifactSetEntity?

    @Query("SELECT id FROM artifact_sets_data")
    suspend fun getAllArtifactSetIds(): List<Int>

    @Query("SELECT * FROM artifact_sets_data WHERE name = :name LIMIT 1")
    suspend fun getSetByName(name: String): ArtifactSetEntity?

    @Upsert
    suspend fun insertArtifactSets(sets: List<ArtifactSetEntity>)

    // --- PIECES ---
    @Query("SELECT * FROM artifact_pieces_data WHERE set_id = :setId")
    fun getPiecesBySetId(setId: Int): Flow<List<ArtifactPieceEntity>>

    @Upsert
    suspend fun insertArtifactPieces(pieces: List<ArtifactPieceEntity>)
}