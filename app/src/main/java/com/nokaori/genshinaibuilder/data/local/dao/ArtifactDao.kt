package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactPieceEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSetEntity
import com.nokaori.genshinaibuilder.data.local.entity.ArtifactSlotRuleEntity
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtifactDao {

    // ========================================================================
    // ТАБЛИЦА СЕТОВ
    // ========================================================================

    /**
     * Получить все сеты. 
     * Возвращает Flow, чтобы UI обновлялся автоматически при загрузке новых данных.
     */
    @Query("SELECT * FROM artifact_sets_data ORDER BY name ASC")
    fun getAllArtifactSets(): Flow<List<ArtifactSetEntity>>

    /**
     * Поиск сетов по названию (для автодополнения или фильтрации).
     * || - это конкатенация строк в SQL.
     */
    @Query("SELECT * FROM artifact_sets_data WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchArtifactSets(query: String): Flow<List<ArtifactSetEntity>>

    /**
     * Получить конкретный сет по ID.
     */
    @Query("SELECT * FROM artifact_sets_data WHERE id = :setId")
    suspend fun getArtifactSetById(setId: Int): ArtifactSetEntity?

    /**
     * Вставка списка сетов (из API).
     * OnConflictStrategy.REPLACE означает: если такой ID уже есть, обновить данные. 
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifactSets(sets: List<ArtifactSetEntity>)

    // ========================================================================
    // ТАБЛИЦА КУСКОВ
    // ========================================================================

    /**
     * Получить все куски конкретного сета.
     */
    @Query("SELECT * FROM artifact_pieces_data WHERE set_id = :setId")
    fun getPiecesBySetId(setId: Int): Flow<List<ArtifactPieceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifactPieces(pieces: List<ArtifactPieceEntity>)

    // ========================================================================
    // ТАБЛИЦА ПРАВИЛ
    // ========================================================================

    /**
     * Получить правила для всех слотов.
     */
    @Query("SELECT * FROM artifact_slot_rules")
    suspend fun getAllSlotRules(): List<ArtifactSlotRuleEntity>

    /**
     * Получить правило для конкретного слота.
     */
    @Query("SELECT * FROM artifact_slot_rules WHERE slot = :slot")
    suspend fun getRulesForSlot(slot: ArtifactSlot): ArtifactSlotRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlotRules(rules: List<ArtifactSlotRuleEntity>)

    /**
     * Получить ID сета по его точному названию.
     */
    @Query("SELECT * FROM artifact_sets_data WHERE name = :name LIMIT 1")
    suspend fun getSetByName(name: String): ArtifactSetEntity?
}