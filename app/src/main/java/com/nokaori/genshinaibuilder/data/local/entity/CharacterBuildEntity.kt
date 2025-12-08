package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSnapshot
import com.nokaori.genshinaibuilder.domain.model.BuildAlertLevel
import com.nokaori.genshinaibuilder.domain.model.BuildRole
import com.nokaori.genshinaibuilder.domain.model.WeaponSnapshot

@Entity(
    tableName = "character_builds",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_encyclopedia_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CharacterBuildEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "build_name")
    val name: String,

    @ColumnInfo(name = "user_description")
    val description: String?,

    @ColumnInfo(name = "build_roles")
    val roles: List<BuildRole>,

    // --- АНАЛИТИКА / ГАЙДЫ ---
    
    // Как играть ЭТИМ персонажем в ЭТОМ билде
    @ColumnInfo(name = "gameplay_guide")
    val gameplayGuide: String?, 

    // Плюсы конкретно этого билда 
    @ColumnInfo(name = "pros")
    val pros: List<String>, 

    // Минусы
    @ColumnInfo(name = "cons")
    val cons: List<String>,

    // Ворнинги
    @ColumnInfo(name = "alert_level")
    val alertLevel: BuildAlertLevel,
    
    @ColumnInfo(name = "alert_message")
    val alertMessage: String?,

    // ---------------------------------------

    @ColumnInfo(name = "character_encyclopedia_id")
    val characterId: Int,

    // --- СНИМОК ХАРАКТЕРИСТИК ---
    val level: Int,
    val ascension: Int,
    val constellation: Int,
    
    @ColumnInfo(name = "talent_normal")
    val talentNormalLevel: Int,
    
    @ColumnInfo(name = "talent_skill")
    val talentSkillLevel: Int,
    
    @ColumnInfo(name = "talent_burst")
    val talentBurstLevel: Int,

    // --- СНИМКИ ОРУЖИЯ И АРТЕФАКТОВ ---
    @ColumnInfo(name = "weapon_snapshot")
    val weaponSnapshot: WeaponSnapshot,

    @ColumnInfo(name = "artifacts_snapshot")
    val artifactsSnapshot: List<ArtifactSnapshot>
)