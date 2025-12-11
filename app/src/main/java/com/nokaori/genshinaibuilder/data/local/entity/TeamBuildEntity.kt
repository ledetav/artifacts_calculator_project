package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.SynergyRating

@Entity(
    tableName = "team_builds",
    foreignKeys = [
        ForeignKey(entity = CharacterBuildEntity::class, parentColumns = ["id"], childColumns = ["slot1_build_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = CharacterBuildEntity::class, parentColumns = ["id"], childColumns = ["slot2_build_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = CharacterBuildEntity::class, parentColumns = ["id"], childColumns = ["slot3_build_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = CharacterBuildEntity::class, parentColumns = ["id"], childColumns = ["slot4_build_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class TeamBuildEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "team_name") val name: String,
    val description: String?,
    @ColumnInfo(name = "rotation_guide") val rotationGuide: String,
    @ColumnInfo(name = "synergy_rating") val synergyRating: SynergyRating, // Enum
    @ColumnInfo(name = "synergy_description") val synergyDescription: String,
    @ColumnInfo(name = "pros") val pros: List<String>,
    @ColumnInfo(name = "cons") val cons: List<String>,
    @ColumnInfo(name = "general_guide") val generalGuide: String?,
    @ColumnInfo(name = "slot1_build_id", index = true) val slot1BuildId: Int?,
    @ColumnInfo(name = "slot2_build_id", index = true) val slot2BuildId: Int?,
    @ColumnInfo(name = "slot3_build_id", index = true) val slot3BuildId: Int?,
    @ColumnInfo(name = "slot4_build_id", index = true) val slot4BuildId: Int?
)