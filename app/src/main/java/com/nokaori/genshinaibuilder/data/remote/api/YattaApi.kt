package com.nokaori.genshinaibuilder.data.remote.api

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponDetailResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface YattaApi {
    @GET("{lang}/avatar")
    suspend fun getAvatarList(@Path("lang") lang: String): YattaResponse<YattaAvatarDto>

    @GET("{lang}/avatar/{id}")
    suspend fun getAvatarDetail(@Path("lang") lang: String, @Path("id") id: String): YattaAvatarDetailResponse

    @GET("static/avatarCurve")
    suspend fun getAvatarCurves(): YattaAvatarCurveResponse

    @GET("static/weaponCurve")
    suspend fun getWeaponCurves(): YattaWeaponCurveResponse

    @GET("static/reliquaryCurve")
    suspend fun getRelicCurves(): YattaRelicCurveResponse

    @GET("{lang}/weapon")
    suspend fun getWeaponList(@Path("lang") lang: String): YattaWeaponResponse

    @GET("{lang}/weapon/{id}")
    suspend fun getWeaponDetail(@Path("lang") lang: String, @Path("id") id: String): YattaWeaponDetailResponse

    @GET("{lang}/reliquary")
    suspend fun getRelicList(@Path("lang") lang: String): YattaRelicResponse

    @GET("{lang}/reliquary/{id}")
    suspend fun getRelicDetail(@Path("lang") lang: String, @Path("id") id: Int): YattaRelicDetailResponse
}