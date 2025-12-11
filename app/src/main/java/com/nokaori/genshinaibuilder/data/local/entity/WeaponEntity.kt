package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

@Entity(tableName = "weapons_data")
data class WeaponEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: WeaponType, // Enum
    val rarity: Int,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "base_atk_lvl1") val baseAtkLvl1: Float,
    @ColumnInfo(name = "sub_stat_type") val subStatType: StatType?,
    @ColumnInfo(name = "sub_stat_base_value") val subStatBaseValue: Float?,
    @ColumnInfo(name = "atk_curve_id") val atkCurveId: String,
    @ColumnInfo(name = "sub_stat_curve_id") val subStatCurveId: String?
)