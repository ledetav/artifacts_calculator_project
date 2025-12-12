package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaCurveResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: Map<String, YattaCurveLevelDto>
)

data class YattaCurveLevelDto(
    @SerializedName("curveInfos") val curveInfos: Map<String, Double>
)