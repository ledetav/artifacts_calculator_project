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
    @Query("SELECT * FROM weapons_data ORDER BY rarity DESC, name ASC")
    fun getAllWeapons(): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data ORDER BY rarity DESC, name ASC")
    fun getAllWeaponsPaging(): PagingSource<Int, WeaponEntity>

    @Query("SELECT icon_url FROM weapons_data")
    suspend fun getAllWeaponUrls(): List<String>

    @Query("SELECT * FROM weapons_data WHERE name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchWeapons(query: String): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data WHERE type = :type ORDER BY rarity DESC")
    fun getWeaponsByType(type: WeaponType): Flow<List<WeaponEntity>>

    @Query("SELECT * FROM weapons_data WHERE id = :weaponId")
    suspend fun getWeaponById(weaponId: Int): WeaponEntity?

    @Query("SELECT id FROM weapons_data")
    suspend fun getAllWeaponIds(): List<Int>

    @Upsert
    suspend fun insertWeapons(weapons: List<WeaponEntity>)

    // --- REFINEMENTS ---
    @Query("SELECT * FROM weapon_refinements WHERE weapon_id = :weaponId")
    suspend fun getWeaponRefinement(weaponId: Int): WeaponRefinementEntity?

    @Upsert
    suspend fun insertRefinements(refinements: List<WeaponRefinementEntity>)

    // --- PROMOTIONS ---
    @Query("SELECT * FROM weapon_promotions WHERE weapon_id = :weaponId AND ascension_level = :ascensionLevel")
    suspend fun getWeaponPromotion(weaponId: Int, ascensionLevel: Int): WeaponPromotionEntity?

    @Upsert
    suspend fun insertPromotions(promotions: List<WeaponPromotionEntity>)
}