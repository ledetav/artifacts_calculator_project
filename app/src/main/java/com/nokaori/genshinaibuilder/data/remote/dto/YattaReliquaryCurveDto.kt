package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

data class YattaRelicCurveResponse(
    @SerializedName("response") val code: Int,
    @SerializedName("data") val data: YattaRelicCurveData
)

data class YattaRelicCurveData(
    @SerializedName("initial") val initial: Map<String, Double>, 
    
    @SerializedName("ranked") val ranked: Map<String, Map<String, Map<String, Double>>>,
    
    @SerializedName("affix") val affix: Map<String, Map<String, List<Double>>>
)