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

    @TypeConverter
    fun fromTalentType(type: com.nokaori.genshinaibuilder.domain.model.TalentType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTalentType(name: String?): com.nokaori.genshinaibuilder.domain.model.TalentType? {
        return name?.let { com.nokaori.genshinaibuilder.domain.model.TalentType.valueOf(it) }
    }

    // Конвертеры для списков TalentAttribute
    @TypeConverter
    fun fromTalentAttributeList(list: List<com.nokaori.genshinaibuilder.domain.model.TalentAttribute>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toTalentAttributeList(json: String): List<com.nokaori.genshinaibuilder.domain.model.TalentAttribute> {
        val type = object : TypeToken<List<com.nokaori.genshinaibuilder.domain.model.TalentAttribute>>() {}.type
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

    // --- БИЛДЫ: Enum Roles ---
    @TypeConverter
    fun fromBuildRoleList(roles: List<BuildRole>?): String {
        return gson.toJson(roles)
    }

    @TypeConverter
    fun toBuildRoleList(json: String): List<BuildRole> {
        val type = object : TypeToken<List<BuildRole>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // --- БИЛДЫ: Alert Level ---
    @TypeConverter
    fun fromBuildAlertLevel(level: BuildAlertLevel): String = level.name

    @TypeConverter
    fun toBuildAlertLevel(name: String): BuildAlertLevel = BuildAlertLevel.valueOf(name)

    // --- БИЛДЫ: Weapon Snapshot ---
    @TypeConverter
    fun fromWeaponSnapshot(snapshot: WeaponSnapshot?): String? {
        return snapshot?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toWeaponSnapshot(json: String?): WeaponSnapshot? {
        return json?.let { gson.fromJson(it, WeaponSnapshot::class.java) }
    }

    // --- БИЛДЫ: Artifacts Snapshot List ---
    @TypeConverter
    fun fromArtifactSnapshotList(list: List<ArtifactSnapshot>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toArtifactSnapshotList(json: String): List<ArtifactSnapshot> {
        val type = object : TypeToken<List<ArtifactSnapshot>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}