package com.nokaori.genshinaibuilder.data.remote.api

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaCurveResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface YattaApi {
    @GET("en/avatar")
    suspend fun getAvatarList(): YattaResponse<YattaAvatarDto>

    @GET("en/avatar/{id}")
    suspend fun getAvatarDetail(@Path("id") id: String): YattaAvatarDetailResponse

    @GET("static/avatarCurve")
    suspend fun getAvatarCurves(): YattaCurveResponse
}