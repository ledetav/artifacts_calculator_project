package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stat_curves")
data class StatCurveEntity(
    @PrimaryKey
    val id: String, // Составной ключ, например "5_CHARACTER_HP"

    val points: Map<Int, Float> 
)