package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artifact_sets_data")
data class ArtifactSetEntity(
    @PrimaryKey
    val id: Int, // из API

    val name: String,

    val rarities: List<Int>, 

    @ColumnInfo(name = "bonus_2pc")
    val bonus2pc: String,

    @ColumnInfo(name = "bonus_4pc")
    val bonus4pc: String,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String
)