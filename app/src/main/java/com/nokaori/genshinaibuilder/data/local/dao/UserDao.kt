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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // --- CHARACTERS ---

    // Для списка "Мои персонажи" (с именем и картинкой из энциклопедии)
    @Transaction
    @Query("SELECT * FROM user_characters")
    fun getAllUserCharactersComplete(): Flow<List<com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete>>

    // Для экрана деталей конкретного перса
    @Transaction
    @Query("SELECT * FROM user_characters WHERE character_encyclopedia_id = :encyclopediaId")
    fun getUserCharacterCompleteByEncyclopediaId(encyclopediaId: Int): Flow<com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete?>

    @Query("SELECT * FROM user_characters WHERE id = :id")
    suspend fun getUserCharacterById(id: Int): UserCharacterEntity?

    @Query("SELECT * FROM user_characters WHERE character_encyclopedia_id = :encyclopediaId")
    suspend fun getUserCharacterByEncyclopediaId(encyclopediaId: Int): UserCharacterEntity?

    // Быстрая проверка
    @Query("SELECT EXISTS(SELECT 1 FROM user_characters WHERE character_encyclopedia_id = :encyclopediaId)")
    suspend fun isCharacterOwned(encyclopediaId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCharacter(character: UserCharacterEntity)

    @Update
    suspend fun updateUserCharacter(character: UserCharacterEntity)

    @Delete
    suspend fun deleteUserCharacter(character: UserCharacterEntity)

    // --- WEAPONS ---

    // Получить оружие + данные из энциклопедии
    @Transaction
    @Query("SELECT * FROM user_weapons")
    fun getUserWeaponsComplete(): Flow<List<com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete>>

    @Query("SELECT * FROM user_weapons WHERE id = :id")
    suspend fun getUserWeaponById(id: Int): UserWeaponEntity?

    @Query("SELECT * FROM user_weapons WHERE equipped_character_id = :charId LIMIT 1")
    fun getWeaponOnCharacter(charId: Int): Flow<UserWeaponEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWeapon(weapon: UserWeaponEntity)

    @Update
    suspend fun updateUserWeapon(weapon: UserWeaponEntity)

    @Delete
    suspend fun deleteUserWeapon(weapon: UserWeaponEntity)

    // --- ARTIFACTS ---

    // (Артефакт + Сет + Имя Куска)
    @Query("""
        SELECT 
            ua.*, 
            s.id AS set_id, s.name AS set_name, s.rarities AS set_rarities, 
            s.bonus_2pc AS set_bonus_2pc, s.bonus_4pc AS set_bonus_4pc, s.icon_url AS set_icon_url,
            p.id AS piece_id, p.set_id AS piece_set_id, p.slot AS piece_slot, 
            p.name AS piece_name, p.icon_url AS piece_icon_url
        FROM user_artifacts AS ua
        INNER JOIN artifact_sets_data AS s ON ua.set_id = s.id
        INNER JOIN artifact_pieces_data AS p ON ua.set_id = p.set_id AND ua.slot = p.slot
    """)
    fun getUserArtifactsComplete(): Flow<List<com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete>>

    @Query("SELECT * FROM user_artifacts WHERE id = :id")
    suspend fun getUserArtifactById(id: Int): UserArtifactEntity?

    @Query("SELECT * FROM user_artifacts WHERE equipped_character_id = :charId")
    fun getArtifactsOnCharacter(charId: Int): Flow<List<UserArtifactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserArtifact(artifact: UserArtifactEntity)

    @Update
    suspend fun updateUserArtifact(artifact: UserArtifactEntity)

    @Delete
    suspend fun deleteUserArtifact(artifact: UserArtifactEntity)
}