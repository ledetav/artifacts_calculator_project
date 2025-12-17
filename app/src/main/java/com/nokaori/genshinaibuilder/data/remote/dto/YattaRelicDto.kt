package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaRelicResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaRelicData
)

data class YattaRelicData(
    @SerializedName("items") val items: Map<String, YattaRelicItemDto>
)

data class YattaRelicItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("levelList") val rarities: List<Int>?,
    @SerializedName("affixList") val bonusMap: Map<String, String>?,
    @SerializedName("icon") val icon: String?
)