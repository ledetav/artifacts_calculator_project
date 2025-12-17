package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaAvatarDetailResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaAvatarDetailDto
)

data class YattaAvatarDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("specialProp") val specialProp: String,
    @SerializedName("upgrade") val upgrade: YattaUpgradeDto,
    @SerializedName("talent") val talents: Map<String, YattaTalentDto>,
    @SerializedName("constellation") val constellations: Map<String, YattaConstellationDto>?
)

data class YattaUpgradeDto(
    @SerializedName("prop") val props: List<YattaPropDto>,
    @SerializedName("promote") val promote: List<YattaPromoteDto>?
)

data class YattaPromoteDto(
    @SerializedName("promoteLevel") val level: Int,
    @SerializedName("addProps") val addProps: Map<String, Double>? 
)

data class YattaPropDto(
    @SerializedName("propType") val propType: String,
    @SerializedName("initValue") val initValue: Double,
    @SerializedName("type") val curveId: String
)

// --- TALENTS ---
data class YattaTalentDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("type") val type: Int,
    @SerializedName("promote") val promote: Map<String, YattaPromoteLevelDto>?,
    @SerializedName("linkedConstellations") val linkedConstellations: YattaLinkedConstsDto?
)

data class YattaPromoteLevelDto(
    @SerializedName("description") val description: List<String>,
    @SerializedName("params") val params: List<Double>
)

data class YattaLinkedConstsDto(
    @SerializedName("prop") val props: List<YattaLinkedPropDto>?
)

data class YattaLinkedPropDto(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String
)

// --- CONSTELLATIONS ---
data class YattaConstellationDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("extraData") val extraData: YattaConstellationExtraDto?
)

// Вот эти классы были потеряны, возвращаем их:
data class YattaConstellationExtraDto(
    @SerializedName("addTalentExtraLevel") val addLevel: YattaAddLevelDto?
)

data class YattaAddLevelDto(
    @SerializedName("talentIndex") val talentIndex: Int?,
    @SerializedName("extraLevel") val extraLevel: Int?
)