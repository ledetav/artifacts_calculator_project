package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_characters",
    foreignKeys = [ForeignKey(entity = CharacterEntity::class, parentColumns = ["id"], childColumns = ["character_encyclopedia_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["character_encyclopedia_id"], unique = true)]
)
data class UserCharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "character_encyclopedia_id") val characterId: Int,
    val level: Int,
    val ascension: Int,
    val constellation: Int,
    @ColumnInfo(name = "talent_normal") val talentNormalLevel: Int,
    @ColumnInfo(name = "talent_skill") val talentSkillLevel: Int,
    @ColumnInfo(name = "talent_burst") val talentBurstLevel: Int
)