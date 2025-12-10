package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType

@Entity(
    tableName = "user_artifacts",
    foreignKeys = [
        // Ссылка на сет из энциклопедии
        ForeignKey(
            entity = ArtifactSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["set_id"],
            onDelete = ForeignKey.CASCADE
        ),
        // Ссылка на Владельца
        ForeignKey(
            entity = UserCharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipped_character_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["set_id"]),
        Index(value = ["equipped_character_id"])
    ]
)
data class UserArtifactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "set_id")
    val setId: Int,

    val slot: ArtifactSlot, // FLOWER, PLUME...
    val rarity: Int,        // 3, 4, 5
    val level: Int,         // 0-20
    
    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean,

    // Главный стат
    @ColumnInfo(name = "main_stat_type")
    val mainStatType: StatType,
    
    @ColumnInfo(name = "main_stat_value")
    val mainStatValue: Float, // Значение

    // Подстаты (JSON List)
    @ColumnInfo(name = "sub_stats")
    val subStats: List<Stat>,

    // Кто носит (если есть)
    @ColumnInfo(name = "equipped_character_id")
    val equippedCharacterId: Int?
)