package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
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
        WHERE c.language = :language
        ORDER BY c.rarity DESC, c.name ASC
    """)
    fun getCharactersWithOwnership(language: String): Flow<List<com.nokaori.genshinaibuilder.data.local.model.CharacterWithOwnership>>

    @Query("SELECT icon_url FROM characters_data WHERE language = :language UNION SELECT splash_url FROM characters_data WHERE language = :language")
    suspend fun getAllCharacterUrls(language: String): List<String>

    @Query("SELECT * FROM characters_data WHERE language = :language AND name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchCharacters(language: String, query: String): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters_data WHERE id = :id AND language = :language")
    suspend fun getCharacterById(id: Int, language: String): CharacterEntity?

    @Query("SELECT DISTINCT id FROM characters_data")
    suspend fun getAllCharacterIds(): List<Int>

    @Upsert
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    // --- PROMOTIONS ---
    @Query("SELECT * FROM character_promotions WHERE character_id = :charId AND language = :language AND ascension_level = :ascension")
    suspend fun getPromotionData(charId: Int, language: String, ascension: Int): CharacterPromotionEntity?

    @Upsert
    suspend fun insertPromotions(promotions: List<CharacterPromotionEntity>)

    @Query("SELECT * FROM character_promotions WHERE character_id = :charId AND language = :language ORDER BY ascension_level ASC")
    suspend fun getPromotionsForCharacter(charId: Int, language: String): List<CharacterPromotionEntity>

    // --- CONSTELLATIONS ---
    @Query("SELECT * FROM character_constellations WHERE character_id = :charId AND language = :language ORDER BY `order` ASC")
    fun getConstellations(charId: Int, language: String): Flow<List<CharacterConstellationEntity>>

    @Upsert
    suspend fun insertConstellations(constellations: List<CharacterConstellationEntity>)

    // --- TALENTS ---
    @Query("SELECT * FROM character_talents WHERE character_id = :charId AND language = :language")
    fun getTalents(charId: Int, language: String): Flow<List<CharacterTalentEntity>>

    @Upsert
    suspend fun insertTalents(talents: List<CharacterTalentEntity>)

    // --- CLEAR CACHE ---
    @Query("DELETE FROM characters_data WHERE language = :language")
    suspend fun clearCharactersByLanguage(language: String)

    @Query("DELETE FROM characters_data")
    suspend fun clearAllCharacters()
}