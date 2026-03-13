package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weapon_refinements",
    primaryKeys = ["weapon_id", "language"],
    foreignKeys = [ForeignKey(entity = WeaponEntity::class, parentColumns = ["id", "language"], childColumns = ["weapon_id", "language"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["weapon_id", "language"])]
)
data class WeaponRefinementEntity(
    @ColumnInfo(name = "weapon_id") val weaponId: Int,
    val language: String,
    @ColumnInfo(name = "passive_name") val passiveName: String,
    val descriptions: List<String> // Converter
)