package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSnapshot
import com.nokaori.genshinaibuilder.domain.model.BuildAlertLevel
import com.nokaori.genshinaibuilder.domain.model.BuildRole
import com.nokaori.genshinaibuilder.domain.model.WeaponSnapshot

@Entity(
    tableName = "character_builds",
    foreignKeys = [ForeignKey(entity = CharacterEntity::class, parentColumns = ["id"], childColumns = ["character_encyclopedia_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["character_encyclopedia_id"])]
)
data class CharacterBuildEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "build_name") val name: String,
    @ColumnInfo(name = "user_description") val description: String?,
    @ColumnInfo(name = "build_roles") val roles: List<BuildRole>, // Converter

    // Analytics
    @ColumnInfo(name = "gameplay_guide") val gameplayGuide: String?,
    @ColumnInfo(name = "pros") val pros: List<String>,
    @ColumnInfo(name = "cons") val cons: List<String>,
    @ColumnInfo(name = "alert_level") val alertLevel: BuildAlertLevel, // Enum
    @ColumnInfo(name = "alert_message") val alertMessage: String?,

    // Character Snapshot
    @ColumnInfo(name = "character_encyclopedia_id") val characterId: Int,
    val level: Int,
    val ascension: Int,
    val constellation: Int,
    @ColumnInfo(name = "talent_normal") val talentNormalLevel: Int,
    @ColumnInfo(name = "talent_skill") val talentSkillLevel: Int,
    @ColumnInfo(name = "talent_burst") val talentBurstLevel: Int,

    // Gear Snapshots
    @ColumnInfo(name = "weapon_snapshot") val weaponSnapshot: WeaponSnapshot, // Converter
    @ColumnInfo(name = "artifacts_snapshot") val artifactsSnapshot: List<ArtifactSnapshot> // Converter
)