package com.nokaori.genshinaibuilder.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType

class Converters {
    private val gson = Gson()

    // --- Списки примитивов ---

    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toIntList(json: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // --- Сложные структуры (Map для кривых) ---

    @TypeConverter
    fun fromCurveMap(map: Map<Int, Float>?): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toCurveMap(json: String): Map<Int, Float> {
        val type = object : TypeToken<Map<Int, Float>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }

    // --- Списки Enums ---
    // Room умеет хранить одиночный Enum, но List<Enum> нужно конвертировать вручную

    @TypeConverter
    fun fromStatTypeList(list: List<StatType>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStatTypeList(json: String): List<StatType> {
        val type = object : TypeToken<List<StatType>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    // Одиночные Enums Room умеет конвертировать сам (в String или Int), но пусть будут заданы явно.
    @TypeConverter
    fun fromElement(element: Element): String = element.name

    @TypeConverter
    fun toElement(name: String): Element = Element.valueOf(name)

    @TypeConverter
    fun fromWeaponType(type: WeaponType): String = type.name

    @TypeConverter
    fun toWeaponType(name: String): WeaponType = WeaponType.valueOf(name)
    
    @TypeConverter
    fun fromArtifactSlot(slot: ArtifactSlot): String = slot.name

    @TypeConverter
    fun toArtifactSlot(name: String): ArtifactSlot = ArtifactSlot.valueOf(name)
}