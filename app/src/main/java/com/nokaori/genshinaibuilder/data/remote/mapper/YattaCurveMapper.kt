package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.StatCurveEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaCurveResponse

fun YattaCurveResponse.toEntities(): List<StatCurveEntity> {
    val tempMap = mutableMapOf<String, MutableMap<Int, Float>>()

    this.data.forEach { (levelStr, levelData) ->
        val level = levelStr.toIntOrNull() ?: return@forEach

        levelData.curveInfos.forEach { (curveId, value) ->
            val curvePoints = tempMap.getOrPut(curveId) { mutableMapOf() }
            curvePoints[level] = value.toFloat()
        }
    }

    return tempMap.map { (id, points) ->
        StatCurveEntity(
            id = id,
            points = points
        )
    }
}