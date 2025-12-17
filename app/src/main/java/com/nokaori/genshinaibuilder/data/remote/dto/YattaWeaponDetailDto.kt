package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaWeaponDetailResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaWeaponDetailDto
)

data class YattaWeaponDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("rank") val rank: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("specialProp") val specialProp: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("description") val description: String?,

    @SerializedName("affix") val affix: Map<String, YattaWeaponAffixDto>?,
    @SerializedName("upgrade") val upgrade: YattaWeaponUpgradeDto? 
)

data class YattaWeaponAffixDto(
    @SerializedName("name") val name: String?,
    @SerializedName("upgrade") val upgrade: Map<String, String>?
)

data class YattaWeaponUpgradeDto(
    @SerializedName("prop") val props: List<YattaWeaponPropDto>?,
    @SerializedName("promote") val promote: List<YattaWeaponPromoteDto>?
)

data class YattaWeaponPropDto(
    @SerializedName("propType") val propType: String?,
    @SerializedName("initValue") val initValue: Double?,
    @SerializedName("type") val curveId: String?
)

data class YattaWeaponPromoteDto(
    @SerializedName("promoteLevel") val level: Int?,
    @SerializedName("addProps") val addProps: Map<String, Double>?
)