package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nokaori.genshinaibuilder.data.local.entity.CharacterConstellationEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterTalentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    // --- BASIC INFO & OWNERSHIP CHECK ---

    // Возвращает список энциклопедии + флаг isOwned (вычисляется через LEFT JOIN)
    @Query("""
        SELECT 
            c.*, 
            CASE WHEN u.id IS NULL THEN 0 ELSE 1 END as isOwned
        FROM characters_data AS c
        LEFT JOIN user_characters AS u ON c.id = u.character_encyclopedia_id
        ORDER BY c.rarity DESC, c.name ASC
    """)
    fun getCharactersWithOwnership(): Flow<List<com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership>>

    @Query("SELECT * FROM characters_data WHERE name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchCharacters(query: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters_data WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    // --- PROMOTIONS ---
    @Query("SELECT * FROM character_promotions WHERE character_id = :charId AND ascension_level = :ascension")
    suspend fun getPromotionData(charId: Int, ascension: Int): CharacterPromotionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<CharacterPromotionEntity>)

    // --- CONSTELLATIONS ---
    @Query("SELECT * FROM character_constellations WHERE character_id = :charId ORDER BY `order` ASC")
    fun getConstellations(charId: Int): Flow<List<CharacterConstellationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstellations(constellations: List<CharacterConstellationEntity>)

    // --- TALENTS ---
    @Query("SELECT * FROM character_talents WHERE character_id = :charId")
    fun getTalents(charId: Int): Flow<List<CharacterTalentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalents(talents: List<CharacterTalentEntity>)
}