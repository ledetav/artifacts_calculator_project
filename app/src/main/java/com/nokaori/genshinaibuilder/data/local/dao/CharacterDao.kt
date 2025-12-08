package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nokaori.genshinaibuilder.data.local.entity.CharacterConstellationEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.CharacterTalentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    // ========================================================================
    // 1. ОСНОВНАЯ ИНФОРМАЦИЯ
    // ========================================================================

    /**
     * Список всех персонажей для главного экрана.
     * Сортировка: 5 звезд -> 4 звезды, внутри по имени.
     */
    @Query("SELECT * FROM characters_data ORDER BY rarity DESC, name ASC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    /**
     * Поиск по имени.
     */
    @Query("SELECT * FROM characters_data WHERE name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchCharacters(query: String): Flow<List<CharacterEntity>>

    /**
     * Получить одного персонажа по ID.
     */
    @Query("SELECT * FROM characters_data WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    // ========================================================================
    // 2. ВОЗВЫШЕНИЯ И СТАТЫ
    // ========================================================================

    /**
     * Получить данные для математики (Base Stats).
     * Нужно знать, сколько HP/ATK/DEF добавить на конкретной фазе возвышения.
     */
    @Query("SELECT * FROM character_promotions WHERE character_id = :charId AND ascension_level = :ascension")
    suspend fun getPromotionData(charId: Int, ascension: Int): CharacterPromotionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<CharacterPromotionEntity>)

    // ========================================================================
    // 3. СОЗВЕЗДИЯ (Constellations)
    // ========================================================================

    /**
     * Получить все созвездия персонажа (от C1 до C6).
     */
    @Query("SELECT * FROM character_constellations WHERE character_id = :charId ORDER BY `order` ASC")
    fun getConstellations(charId: Int): Flow<List<CharacterConstellationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstellations(constellations: List<CharacterConstellationEntity>)

    // ========================================================================
    // 4. ТАЛАНТЫ (Talents)
    // ========================================================================

    /**
     * Получить все таланты персонажа.
     */
    @Query("SELECT * FROM character_talents WHERE character_id = :charId")
    fun getTalents(charId: Int): Flow<List<CharacterTalentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalents(talents: List<CharacterTalentEntity>)
}