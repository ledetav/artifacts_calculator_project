package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaWeaponResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaWeaponData
)

data class YattaWeaponData(
    @SerializedName("items") val items: Map<String, YattaWeaponItemDto> 
)

data class YattaWeaponItemDto(
    @SerializedName("id") val id: String, 
    @SerializedName("rank") val rank: Int?, 
    @SerializedName("type") val type: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("specialProp") val specialProp: String?,
    @SerializedName("icon") val icon: String?, 
    @SerializedName("isWeaponSkin") val isWeaponSkin: Boolean? 
)