package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_constellations",
    primaryKeys = ["character_id", "order"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["character_id"])]
)
data class CharacterConstellationEntity(
    @ColumnInfo(name = "character_id")
    val characterId: Int,

    val order: Int, // 1..6

    val name: String,
    val description: String,
    
    @ColumnInfo(name = "icon_url")
    val iconUrl: String
    
    // skill_level_bonus добавить позже, решить, как кодировать.
)