package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_weapons",
    foreignKeys = [
        ForeignKey(entity = WeaponEntity::class, parentColumns = ["id"], childColumns = ["weapon_encyclopedia_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = UserCharacterEntity::class, parentColumns = ["id"], childColumns = ["equipped_character_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index(value = ["weapon_encyclopedia_id"]), Index(value = ["equipped_character_id"])]
)
data class UserWeaponEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "weapon_encyclopedia_id") val weaponId: Int,
    val level: Int,
    val ascension: Int,
    val refinement: Int,
    @ColumnInfo(name = "is_locked") val isLocked: Boolean,
    @ColumnInfo(name = "equipped_character_id") val equippedCharacterId: Int?
)