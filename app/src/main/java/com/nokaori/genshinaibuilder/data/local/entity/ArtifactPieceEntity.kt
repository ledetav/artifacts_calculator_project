package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot

@Entity(
    tableName = "artifact_pieces_data",
    foreignKeys = [ForeignKey(entity = ArtifactSetEntity::class, parentColumns = ["id"], childColumns = ["set_id"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["set_id"])]
)
data class ArtifactPieceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "set_id") val setId: Int,
    val slot: ArtifactSlot, // Enum
    val name: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String
)