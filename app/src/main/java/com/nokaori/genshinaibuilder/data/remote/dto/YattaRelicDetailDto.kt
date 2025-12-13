package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaRelicDetailResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaRelicDetailDto
)

data class YattaRelicDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("levelList") val rarities: List<Int>?,
    @SerializedName("affixList") val bonusMap: Map<String, String>?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("suit") val suit: Map<String, YattaRelicPieceDto>?
)

data class YattaRelicPieceDto(
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String
)