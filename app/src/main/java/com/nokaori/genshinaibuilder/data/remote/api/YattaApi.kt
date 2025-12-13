package com.nokaori.genshinaibuilder.data.remote.api

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDetailResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface YattaApi {
    @GET("en/avatar")
    suspend fun getAvatarList(): YattaResponse<YattaAvatarDto>

    @GET("en/avatar/{id}")
    suspend fun getAvatarDetail(@Path("id") id: String): YattaAvatarDetailResponse

    @GET("static/avatarCurve")
    suspend fun getAvatarCurves(): YattaAvatarCurveResponse

    @GET("static/weaponCurve")
    suspend fun getWeaponCurves(): YattaWeaponCurveResponse

    @GET("static/reliquaryCurve")
    suspend fun getRelicCurves(): YattaRelicCurveResponse

    @GET("en/weapon")
    suspend fun getWeaponList(): YattaWeaponResponse

    @GET("en/weapon/{id}")
    suspend fun getWeaponDetail(@Path("id") id: String): YattaWeaponDetailResponse
}