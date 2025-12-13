package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtifactDao {
    // --- SETS ---
    @Query("SELECT * FROM artifact_sets_data ORDER BY name ASC")
    fun getAllArtifactSets(): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtifactSets(query: String): Flow<List<ArtifactSetEntity>>

    @Query("SELECT * FROM artifact_sets_data WHERE id = :setId")
    suspend fun getArtifactSetById(setId: Int): ArtifactSetEntity?

    // Критически важно для добавления артефакта вручную (поиск ID по имени)
    @Query("SELECT * FROM artifact_sets_data WHERE name = :name LIMIT 1")
    suspend fun getSetByName(name: String): ArtifactSetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifactSets(sets: List<ArtifactSetEntity>)

    // --- PIECES ---
    @Query("SELECT * FROM artifact_pieces_data WHERE set_id = :setId")
    fun getPiecesBySetId(setId: Int): Flow<List<ArtifactPieceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifactPieces(pieces: List<ArtifactPieceEntity>)
}