package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaWeaponCurveResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: Map<String, YattaWeaponCurveLevelDto> // Key: Level "1".."90"
)

data class YattaWeaponCurveLevelDto(
    @SerializedName("curveInfos") val curveInfos: Map<String, Double>
)