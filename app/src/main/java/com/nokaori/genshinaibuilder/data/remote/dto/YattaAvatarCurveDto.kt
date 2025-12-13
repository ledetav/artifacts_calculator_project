package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaAvatarCurveResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: Map<String, YattaAvatarCurveLevelDto> // Ключ: Уровень ("1".."90")
)

data class YattaAvatarCurveLevelDto(
    @SerializedName("curveInfos") val curveInfos: Map<String, Double>
)