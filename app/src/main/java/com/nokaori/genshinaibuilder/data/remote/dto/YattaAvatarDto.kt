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
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("rank") val rank: Int,
    @SerializedName("element") val element: String, // "Ice", "Wind"...
    @SerializedName("weaponType") val weaponType: String, // "WEAPON_POLE"...
    @SerializedName("icon") val iconName: String, // "UI_AvatarIcon_Ayaka"
    @SerializedName("release") val releaseDate: Long
)