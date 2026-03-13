package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_promotions",
    primaryKeys = ["character_id", "language", "ascension_level"],
    foreignKeys = [ForeignKey(entity = CharacterEntity::class, parentColumns = ["id", "language"], childColumns = ["character_id", "language"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["character_id", "language"])]
)
data class CharacterPromotionEntity(
    @ColumnInfo(name = "character_id") val characterId: Int,
    val language: String,
    @ColumnInfo(name = "ascension_level") val ascensionLevel: Int,
    @ColumnInfo(name = "add_hp") val addHp: Float,
    @ColumnInfo(name = "add_atk") val addAtk: Float,
    @ColumnInfo(name = "add_def") val addDef: Float,
    @ColumnInfo(name = "ascension_stat_value") val ascensionStatValue: Float
)