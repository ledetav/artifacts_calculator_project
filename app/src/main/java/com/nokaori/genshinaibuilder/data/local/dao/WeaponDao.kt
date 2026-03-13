package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import kotlinx.coroutines.flow.Flow
import androidx.paging.PagingSource

@Dao
interface WeaponDao {
    // --- ENCYCLOPEDIA ---
    @Query("SELECT * FROM weapons_data WHERE language = :language ORDER BY rarity DESC, name ASC")
    fun getAllWeapons(language: String): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data WHERE language = :language ORDER BY rarity DESC, name ASC")
    fun getAllWeaponsPaging(language: String): PagingSource<Int, WeaponEntity>

    @Query("SELECT icon_url FROM weapons_data WHERE language = :language")
    suspend fun getAllWeaponUrls(language: String): List<String>

    @Query("SELECT * FROM weapons_data WHERE language = :language AND name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchWeapons(language: String, query: String): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data WHERE language = :language AND type = :type ORDER BY rarity DESC")
    fun getWeaponsByType(language: String, type: WeaponType): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data WHERE id = :weaponId AND language = :language")
    suspend fun getWeaponById(weaponId: Int, language: String): WeaponEntity?

    @Query("SELECT DISTINCT id FROM weapons_data")
    suspend fun getAllWeaponIds(): List<Int>

    @Upsert
    suspend fun insertWeapons(weapons: List<WeaponEntity>)

    // --- REFINEMENTS ---
    @Query("SELECT * FROM weapon_refinements WHERE weapon_id = :weaponId AND language = :language")
    suspend fun getWeaponRefinement(weaponId: Int, language: String): WeaponRefinementEntity?

    @Upsert
    suspend fun insertRefinements(refinements: List<WeaponRefinementEntity>)

    // --- PROMOTIONS ---
    @Query("SELECT * FROM weapon_promotions WHERE weapon_id = :weaponId AND language = :language AND ascension_level = :ascensionLevel")
    suspend fun getWeaponPromotion(weaponId: Int, language: String, ascensionLevel: Int): WeaponPromotionEntity?

    @Upsert
    suspend fun insertPromotions(promotions: List<WeaponPromotionEntity>)

    // --- CLEAR CACHE ---
    @Query("DELETE FROM weapons_data WHERE language = :language")
    suspend fun clearWeaponsByLanguage(language: String)

    @Query("DELETE FROM weapons_data")
    suspend fun clearAllWeapons()
}