package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaAvatarDetailResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaAvatarDetailDto
)

data class YattaAvatarDetailDto(
    @SerializedName("id") val id: String,
    @SerializedName("specialProp") val specialProp: String,
    @SerializedName("upgrade") val upgrade: YattaUpgradeDto
)

data class YattaUpgradeDto(
    @SerializedName("prop") val props: List<YattaPropDto>
)

data class YattaPropDto(
    @SerializedName("propType") val propType: String, 
    @SerializedName("initValue") val initValue: Double,
    @SerializedName("type") val curveId: String
)