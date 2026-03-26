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
import com.nokaori.genshinaibuilder.data.local.model.UserCharacterComplete
import com.nokaori.genshinaibuilder.data.local.model.UserWeaponComplete
import com.nokaori.genshinaibuilder.data.local.model.UserArtifactComplete
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // --- CHARACTERS ---

    // Для списка "Мои персонажи" (с именем и картинкой из энциклопедии)
    @Query("""
        SELECT 
            uc.*, 
            c.id AS char_dict_id, c.language AS char_dict_language, c.name AS char_dict_name, 
            c.rarity AS char_dict_rarity, c.element AS char_dict_element, c.weapon_type AS char_dict_weapon_type, 
            c.icon_url AS char_dict_icon_url, c.base_hp_lvl1 AS char_dict_base_hp_lvl1, c.base_atk_lvl1 AS char_dict_base_atk_lvl1, 
            c.base_def_lvl1 AS char_dict_base_def_lvl1, c.hp_curve_id AS char_dict_hp_curve_id, 
            c.atk_curve_id AS char_dict_atk_curve_id, c.def_curve_id AS char_dict_def_curve_id, 
            c.specialized_stat_type AS char_dict_specialized_stat_type, c.specialized_stat_curve_id AS char_dict_specialized_stat_curve_id
        FROM user_characters AS uc
        INNER JOIN characters_data AS c ON uc.character_encyclopedia_id = c.id AND c.language = :language
    """)
    fun getAllUserCharactersComplete(language: String): Flow<List<UserCharacterComplete>>

    // Для экрана деталей конкретного перса
    @Query("""
        SELECT 
            uc.*, 
            c.id AS char_dict_id, c.language AS char_dict_language, c.name AS char_dict_name, 
            c.rarity AS char_dict_rarity, c.element AS char_dict_element, c.weapon_type AS char_dict_weapon_type, 
            c.icon_url AS char_dict_icon_url, c.base_hp_lvl1 AS char_dict_base_hp_lvl1, c.base_atk_lvl1 AS char_dict_base_atk_lvl1, 
            c.base_def_lvl1 AS char_dict_base_def_lvl1, c.hp_curve_id AS char_dict_hp_curve_id, 
            c.atk_curve_id AS char_dict_atk_curve_id, c.def_curve_id AS char_dict_def_curve_id, 
            c.specialized_stat_type AS char_dict_specialized_stat_type, c.specialized_stat_curve_id AS char_dict_specialized_stat_curve_id
        FROM user_characters AS uc
        INNER JOIN characters_data AS c ON uc.character_encyclopedia_id = c.id AND c.language = :language
        WHERE uc.character_encyclopedia_id = :encyclopediaId
    """)
    fun getUserCharacterCompleteByEncyclopediaId(encyclopediaId: Int, language: String): Flow<UserCharacterComplete?>

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
    @Query("""
        SELECT 
            uw.*, 
            w.id AS weapon_dict_id, w.language AS weapon_dict_language, w.name AS weapon_dict_name, 
            w.type AS weapon_dict_type, w.rarity AS weapon_dict_rarity, w.icon_url AS weapon_dict_icon_url, 
            w.base_atk_lvl1 AS weapon_dict_base_atk_lvl1, w.sub_stat_type AS weapon_dict_sub_stat_type, 
            w.sub_stat_base_value AS weapon_dict_sub_stat_base_value, w.atk_curve_id AS weapon_dict_atk_curve_id, 
            w.sub_stat_curve_id AS weapon_dict_sub_stat_curve_id
        FROM user_weapons AS uw
        INNER JOIN weapons_data AS w ON uw.weapon_encyclopedia_id = w.id AND w.language = :language
    """)
    fun getUserWeaponsComplete(language: String): Flow<List<UserWeaponComplete>>

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
            s.id AS user_set_id, s.language AS user_set_language, s.name AS user_set_name, s.rarities AS user_set_rarities, 
            s.bonus_2pc AS user_set_bonus_2pc, s.bonus_4pc AS user_set_bonus_4pc, s.icon_url AS user_set_icon_url,
            p.id AS piece_id, p.language AS piece_language, p.set_id AS piece_set_id, p.slot AS piece_slot, 
            p.name AS piece_name, p.icon_url AS piece_icon_url
        FROM user_artifacts AS ua
        INNER JOIN artifact_sets_data AS s ON ua.set_id = s.id AND s.language = :language
        INNER JOIN artifact_pieces_data AS p ON ua.set_id = p.set_id AND ua.slot = p.slot AND p.language = :language
    """)
    fun getUserArtifactsComplete(language: String): Flow<List<UserArtifactComplete>>

    @Query("""
        SELECT 
            ua.*, 
            s.id AS user_set_id, s.language AS user_set_language, s.name AS user_set_name, s.rarities AS user_set_rarities, 
            s.bonus_2pc AS user_set_bonus_2pc, s.bonus_4pc AS user_set_bonus_4pc, s.icon_url AS user_set_icon_url,
            p.id AS piece_id, p.language AS piece_language, p.set_id AS piece_set_id, p.slot AS piece_slot, 
            p.name AS piece_name, p.icon_url AS piece_icon_url
        FROM user_artifacts AS ua
        INNER JOIN artifact_sets_data AS s ON ua.set_id = s.id AND s.language = :language
        INNER JOIN artifact_pieces_data AS p ON ua.set_id = p.set_id AND ua.slot = p.slot AND p.language = :language
        WHERE ua.id = :id
    """)
    suspend fun getUserArtifactCompleteById(id: Int, language: String): UserArtifactComplete?

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