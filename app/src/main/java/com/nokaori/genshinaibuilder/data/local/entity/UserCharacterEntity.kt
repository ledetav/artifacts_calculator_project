package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_characters",
    foreignKeys = [
        // Связь с энциклопедией
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_encyclopedia_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Уникальный индекс: нельзя добавить двух одинаковых персонажей
    indices = [Index(value = ["character_encyclopedia_id"], unique = true)]
)
data class UserCharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Внутренний ID

    @ColumnInfo(name = "character_encyclopedia_id")
    val characterId: Int, // ID из API

    val level: Int,
    val ascension: Int, // 0-6
    val constellation: Int, // 0-6

    // Уровни талантов
    // "базовый" уровень прокачки, +3 от созвездий считать в коде.
    // Пока будем хранить уровень ПРОКАЧКИ (1-10)).
    @ColumnInfo(name = "talent_normal")
    val talentNormalLevel: Int,

    @ColumnInfo(name = "talent_skill")
    val talentSkillLevel: Int,

    @ColumnInfo(name = "talent_burst")
    val talentBurstLevel: Int
)