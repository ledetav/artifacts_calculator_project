package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.TalentAttribute
import com.nokaori.genshinaibuilder.domain.model.TalentType

@Entity(
    tableName = "character_talents",
    primaryKeys = ["character_id", "language", "order_index"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class, parentColumns = ["id", "language"],
            childColumns = ["character_id", "language"], onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["character_id", "language"])]
)
data class CharacterTalentEntity(
    @ColumnInfo(name = "character_id") val characterId: Int,
    val language: String,
    @ColumnInfo(name = "order_index") val orderIndex: Int, 
    @ColumnInfo(name = "type") val type: TalentType,
    val name: String,
    val description: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String,
    @ColumnInfo(name = "scaling_attributes") val scalingAttributes: List<TalentAttribute>
)