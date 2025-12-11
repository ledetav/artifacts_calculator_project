package com.nokaori.genshinaibuilder.data.remote.api

import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarDto
import com.nokaori.genshinaibuilder.data.remote.dto.YattaResponse
import retrofit2.http.GET

interface YattaApi {
    @GET("en/avatar")
    suspend fun getAvatarList(): YattaResponse<YattaAvatarDto>
}