package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "weapon_refinements",
    foreignKeys = [ForeignKey(entity = WeaponEntity::class, parentColumns = ["id"], childColumns = ["weapon_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["weapon_id"])]
)
data class WeaponRefinementEntity(
    @PrimaryKey @ColumnInfo(name = "weapon_id") val weaponId: Int,
    @ColumnInfo(name = "passive_name") val passiveName: String,
    val descriptions: List<String> // Converter
)