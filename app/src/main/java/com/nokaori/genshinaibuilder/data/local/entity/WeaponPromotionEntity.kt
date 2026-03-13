package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "weapon_promotions",
    primaryKeys = ["weapon_id", "language", "ascension_level"],
    foreignKeys = [ForeignKey(entity = WeaponEntity::class, parentColumns = ["id", "language"], childColumns = ["weapon_id", "language"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["weapon_id", "language"])]
)
data class WeaponPromotionEntity(
    @ColumnInfo(name = "weapon_id") val weaponId: Int,
    val language: String,
    @ColumnInfo(name = "ascension_level") val ascensionLevel: Int,
    @ColumnInfo(name = "add_atk") val addAtk: Float,
    @ColumnInfo(name = "add_sub_stat") val addSubStat: Float?
)