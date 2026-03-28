package com.nokaori.genshinaibuilder.data.local.entity

import androidx.room.*

@Entity(tableName = "sync_metadata")
data class SyncMetadataEntity(
    @PrimaryKey val key: String,
    val value: Long
)
