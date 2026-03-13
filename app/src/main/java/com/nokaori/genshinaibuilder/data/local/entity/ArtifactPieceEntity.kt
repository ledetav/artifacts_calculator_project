package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot

@Entity(
    tableName = "artifact_pieces_data",
    primaryKeys = ["id", "language"],
    foreignKeys = [ForeignKey(entity = ArtifactSetEntity::class, parentColumns = ["id", "language"], childColumns = ["set_id", "language"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["set_id", "language"])]
)
data class ArtifactPieceEntity(
    val id: Int,
    val language: String,
    @ColumnInfo(name = "set_id") val setId: Int,
    val slot: ArtifactSlot, // Enum
    val name: String,
    @ColumnInfo(name = "icon_url") val iconUrl: String
)