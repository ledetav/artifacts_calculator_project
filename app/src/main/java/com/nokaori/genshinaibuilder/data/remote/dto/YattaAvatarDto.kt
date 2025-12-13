package com.nokaori.genshinaibuilder.data.remote.dto

import com.google.gson.annotations.SerializedName

// Обертка ответа (Root)
data class YattaResponse<T>(
        @SerializedName("response") val code: Int,
        @SerializedName("data") val data: YattaData<T>
)

// Обертка данных
data class YattaData<T>(
        @SerializedName("items") val items: Map<String, T> // Апи возвращает Map
)

// Сам объект персонажа из JSON
data class YattaAvatarDto(
        @SerializedName("id") val id: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("rank") val rank: Int?,
        @SerializedName("element") val element: String?,
        @SerializedName("weaponType") val weaponType: String?,
        @SerializedName("icon") val iconName: String?,
        @SerializedName("release") val releaseDate: Long?
)