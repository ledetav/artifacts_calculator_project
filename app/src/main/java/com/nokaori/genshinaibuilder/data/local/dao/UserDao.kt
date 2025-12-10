package com.nokaori.genshinaibuilder.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.nokaori.genshinaibuilder.data.local.entity.UserArtifactEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserCharacterEntity
import com.nokaori.genshinaibuilder.data.local.entity.UserWeaponEntity
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactWithSet
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ========================================================================
    // 1. МОИ ПЕРСОНАЖИ
    // ========================================================================

    /**
     * Получить список всех моих персонажей.
     */
    @Query("SELECT * FROM user_characters")
    fun getAllUserCharacters(): Flow<List<UserCharacterEntity>>

    /**
     * Получить конкретного персонажа из инвентаря по его ID.
     */
    @Query("SELECT * FROM user_characters WHERE id = :id")
    suspend fun getUserCharacterById(id: Int): UserCharacterEntity?

    /**
     * Проверить, есть ли у нас уже этот персонаж (по ID энциклопедии).
     * Нужно, чтобы не добавить второго такого же.
     */
    @Query("SELECT * FROM user_characters WHERE character_encyclopedia_id = :encyclopediaId")
    suspend fun getUserCharacterByEncyclopediaId(encyclopediaId: Int): UserCharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCharacter(character: UserCharacterEntity)

    @Update
    suspend fun updateUserCharacter(character: UserCharacterEntity)

    @Delete
    suspend fun deleteUserCharacter(character: UserCharacterEntity)


    // ========================================================================
    // 2. МОЕ ОРУЖИЕ
    // ========================================================================

    @Query("SELECT * FROM user_weapons")
    fun getAllUserWeapons(): Flow<List<UserWeaponEntity>>

    @Query("SELECT * FROM user_weapons WHERE id = :id")
    suspend fun getUserWeaponById(id: Int): UserWeaponEntity?

    /**
     * Найти оружие, надетое на конкретного персонажа.
     * Возвращает 1 объект.
     */
    @Query("SELECT * FROM user_weapons WHERE equipped_character_id = :charId LIMIT 1")
    fun getWeaponOnCharacter(charId: Int): Flow<UserWeaponEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWeapon(weapon: UserWeaponEntity)

    @Update
    suspend fun updateUserWeapon(weapon: UserWeaponEntity)

    @Delete
    suspend fun deleteUserWeapon(weapon: UserWeaponEntity)


    // ========================================================================
    // 3. МОИ АРТЕФАКТЫ
    // ========================================================================

    @Query("SELECT * FROM user_artifacts")
    fun getAllUserArtifacts(): Flow<List<UserArtifactEntity>>

    @Query("SELECT * FROM user_artifacts WHERE id = :id")
    suspend fun getUserArtifactById(id: Int): UserArtifactEntity?

    /**
     * Получить все артефакты, надетые на персонажа.
     */
    @Query("SELECT * FROM user_artifacts WHERE equipped_character_id = :charId")
    fun getArtifactsOnCharacter(charId: Int): Flow<List<UserArtifactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserArtifact(artifact: UserArtifactEntity)

    @Update
    suspend fun updateUserArtifact(artifact: UserArtifactEntity)

    @Delete
    suspend fun deleteUserArtifact(artifact: UserArtifactEntity)

    @Transaction // Обязательно для @Relation
    @Query("SELECT * FROM user_artifacts")
    fun getUserArtifactsWithSets(): Flow<List<UserArtifactWithSet>>

    /**
     * Получить полные данные об артефактах пользователя:
     * Инвентарь + Инфо о Сете + Инфо о конкретном Куске (Имя, иконка).
     */
    @Query("""
        SELECT 
            ua.*,
            s.id AS set_id,
            s.name AS set_name,
            s.rarities AS set_rarities,
            s.bonus_2pc AS set_bonus_2pc,
            s.bonus_4pc AS set_bonus_4pc,
            s.icon_url AS set_icon_url,

            p.id AS piece_id,
            p.set_id AS piece_set_id,
            p.slot AS piece_slot,
            p.name AS piece_name,
            p.icon_url AS piece_icon_url
            
        FROM user_artifacts AS ua

        INNER JOIN artifact_sets_data AS s ON ua.set_id = s.id

        INNER JOIN artifact_pieces_data AS p ON ua.set_id = p.set_id AND ua.slot = p.slot
    """)
    fun getUserArtifactsComplete(): Flow<List<UserArtifactComplete>>

    // Добавляем Transaction, так как @Relation делает два запроса под капотом
    @Transaction
    @Query("SELECT * FROM user_weapons")
    fun getUserWeaponsComplete(): Flow<List<UserWeaponComplete>>
}