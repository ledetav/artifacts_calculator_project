package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.StatType

@Entity(tableName = "artifact_slot_rules")
data class ArtifactSlotRuleEntity(
    @PrimaryKey val slot: ArtifactSlot, // PK
    @ColumnInfo(name = "allowed_main_stats") val allowedMainStats: List<StatType> // Converter
)