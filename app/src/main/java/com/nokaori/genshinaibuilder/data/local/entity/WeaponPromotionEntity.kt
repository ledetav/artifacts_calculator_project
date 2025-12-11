package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "weapon_promotions",
    primaryKeys = ["weapon_id", "ascension_level"],
    foreignKeys = [ForeignKey(entity = WeaponEntity::class, parentColumns = ["id"], childColumns = ["weapon_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["weapon_id"])]
)
data class WeaponPromotionEntity(
    @ColumnInfo(name = "weapon_id") val weaponId: Int,
    @ColumnInfo(name = "ascension_level") val ascensionLevel: Int,
    @ColumnInfo(name = "add_atk") val addAtk: Float,
    @ColumnInfo(name = "add_sub_stat") val addSubStat: Float?
)