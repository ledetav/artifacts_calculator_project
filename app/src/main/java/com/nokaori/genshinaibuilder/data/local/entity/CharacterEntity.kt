package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

@Entity(tableName = "characters_data")
data class CharacterEntity(
    @PrimaryKey
    val id: Int, //bиз API

    val name: String,
    
    val rarity: Int,

    val element: Element, // Enum

    @ColumnInfo(name = "weapon_type")
    val weaponType: WeaponType, // Enum

    // Базовые статы на 1 уровне
    @ColumnInfo(name = "base_hp_lvl1")
    val baseHpLvl1: Float,

    @ColumnInfo(name = "base_atk_lvl1")
    val baseAtkLvl1: Float,

    @ColumnInfo(name = "base_def_lvl1")
    val baseDefLvl1: Float,

    // Какой стат качается при возвышении (например, CRIT_DMG)
    @ColumnInfo(name = "ascension_stat_type")
    val ascensionStatType: StatType,

    // Ссылка на кривую роста (в таблицу stat_curves)
    @ColumnInfo(name = "curve_id")
    val curveId: String,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String,

    @ColumnInfo(name = "splash_url")
    val splashUrl: String
)