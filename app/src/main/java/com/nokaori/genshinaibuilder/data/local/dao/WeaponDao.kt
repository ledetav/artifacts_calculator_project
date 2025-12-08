package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nokaori.genshinaibuilder.data.local.entity.WeaponEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponPromotionEntity
import com.nokaori.genshinaibuilder.data.local.entity.WeaponRefinementEntity
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import kotlinx.coroutines.flow.Flow

@Dao
interface WeaponDao {

    // ========================================================================
    // ТАБЛИЦА ОРУЖИЯ
    // ========================================================================

    /**
     * Получить все оружие. Сортируем: сначала 5 звезд, потом по алфавиту.
     */
    @Query("SELECT * FROM weapons_data ORDER BY rarity DESC, name ASC")
    fun getAllWeapons(): Flow<List<WeaponEntity>>

    /**
     * Поиск по имени.
     */
    @Query("SELECT * FROM weapons_data WHERE name LIKE '%' || :query || '%' ORDER BY rarity DESC")
    fun searchWeapons(query: String): Flow<List<WeaponEntity>>

    /**
     * Фильтр по типу оружия.
     */
    @Query("SELECT * FROM weapons_data WHERE type = :type ORDER BY rarity DESC")
    fun getWeaponsByType(type: WeaponType): Flow<List<WeaponEntity>>

    /**
     * Получить конкретное оружие по ID.
     */
    @Query("SELECT * FROM weapons_data WHERE id = :weaponId")
    suspend fun getWeaponById(weaponId: Int): WeaponEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeapons(weapons: List<WeaponEntity>)

    // ========================================================================
    // ТАБЛИЦА ПАССИВОК
    // ========================================================================

    /**
     * Получить пассивку оружия
     */
    @Query("SELECT * FROM weapon_refinements WHERE weapon_id = :weaponId")
    suspend fun getWeaponRefinement(weaponId: Int): WeaponRefinementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefinements(refinements: List<WeaponRefinementEntity>)

    // ========================================================================
    // ТАБЛИЦА ВОЗВЫШЕНИЙ
    // ========================================================================

    /**
     * Получить данные о возвышении для конкретного уровня (фазы).
     * Это нужно для UseCase рассчета статов: BaseAtk + PromotionBonus.
     */
    @Query("SELECT * FROM weapon_promotions WHERE weapon_id = :weaponId AND ascension_level = :ascensionLevel")
    suspend fun getWeaponPromotion(weaponId: Int, ascensionLevel: Int): WeaponPromotionEntity?

    /**
     * Получить все данные возвышения для оружия.
     */
    @Query("SELECT * FROM weapon_promotions WHERE weapon_id = :weaponId")
    suspend fun getAllPromotionsForWeapon(weaponId: Int): List<WeaponPromotionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotions(promotions: List<WeaponPromotionEntity>)
}