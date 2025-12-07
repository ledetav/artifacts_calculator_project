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
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["character_id"])]
)
data class CharacterTalentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Внутренний ID базы

    @ColumnInfo(name = "character_id")
    val characterId: Int,

    val type: TalentType,

    val name: String,
    val description: String,
    
    @ColumnInfo(name = "icon_url")
    val iconUrl: String,

    // Самое важное поле: JSON список цифр урона
    @ColumnInfo(name = "scaling_attributes")
    val scalingAttributes: List<TalentAttribute>
)