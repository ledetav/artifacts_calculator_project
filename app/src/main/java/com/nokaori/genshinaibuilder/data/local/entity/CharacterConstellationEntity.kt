package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.nokaori.genshinaibuilder.domain.model.TalentType

@Entity(
    tableName = "character_constellations",
    primaryKeys = ["character_id", "order"],
    foreignKeys = [ForeignKey(entity = CharacterEntity::class, parentColumns = ["id"], childColumns = ["character_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["character_id"])]
)
data class CharacterConstellationEntity(
    @ColumnInfo(name = "character_id") val characterId: Int,
    val order: Int,
    val name: String,
    val description: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "talent_level_up_target") val talentLevelUpTarget: TalentType? // Enum
)