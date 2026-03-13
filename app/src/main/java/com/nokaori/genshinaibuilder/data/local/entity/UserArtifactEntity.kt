package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType

@Entity(
    tableName = "user_artifacts",
    foreignKeys = [
        ForeignKey(entity = UserCharacterEntity::class, parentColumns = ["id"], childColumns = ["equipped_character_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index(value = ["set_id"]), Index(value = ["equipped_character_id"])]
)
data class UserArtifactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "set_id") val setId: Int,
    val slot: ArtifactSlot,
    val rarity: Int,
    val level: Int,
    @ColumnInfo(name = "is_locked") val isLocked: Boolean,
    @ColumnInfo(name = "main_stat_type") val mainStatType: StatType,
    @ColumnInfo(name = "main_stat_value") val mainStatValue: Float,
    @ColumnInfo(name = "sub_stats") val subStats: List<Stat>, // Converter
    @ColumnInfo(name = "equipped_character_id") val equippedCharacterId: Int?
)