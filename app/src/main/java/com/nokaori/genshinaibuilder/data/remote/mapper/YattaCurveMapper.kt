package com.nokaori.genshinaibuilder.data.remote.mapper

import com.nokaori.genshinaibuilder.data.local.entity.StatCurveEntity
import com.nokaori.genshinaibuilder.data.remote.dto.YattaAvatarCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaRelicCurveResponse
import com.nokaori.genshinaibuilder.data.remote.dto.YattaWeaponCurveResponse

fun YattaAvatarCurveResponse.toEntities(): List<StatCurveEntity> {
    val tempMap = mutableMapOf<String, MutableMap<Int, Float>>()
    
    this.data.forEach { (levelStr: String, levelData) ->
        val level = levelStr.toIntOrNull() ?: return@forEach
        
        levelData.curveInfos.forEach { (curveId: String, value: Double) ->
            tempMap.getOrPut(curveId) { mutableMapOf() }[level] = value.toFloat()
        }
    }
    return tempMap.map { StatCurveEntity(it.key, it.value) }
}

fun YattaWeaponCurveResponse.toEntities(): List<StatCurveEntity> {
    val tempMap = mutableMapOf<String, MutableMap<Int, Float>>()
    this.data.forEach { (levelStr: String, levelData) ->
        val level = levelStr.toIntOrNull() ?: return@forEach
        levelData.curveInfos.forEach { (curveId: String, value: Double) ->
            tempMap.getOrPut(curveId) { mutableMapOf() }[level] = value.toFloat()
        }
    }
    return tempMap.map { StatCurveEntity(it.key, it.value) }
}

fun YattaRelicCurveResponse.toEntities(): List<StatCurveEntity> {
    val result = mutableListOf<StatCurveEntity>()

    this.data.ranked.forEach ranked@{ (rankStr: String, levelsMap) ->
        val rank = rankStr.toIntOrNull() ?: return@ranked
        val mainStatsMap = mutableMapOf<String, MutableMap<Int, Float>>()

        levelsMap.forEach levels@{ (levelStr: String, stats) ->
            val gameLevel = (levelStr.toIntOrNull() ?: 0) - 1
            if (gameLevel < 0) return@levels
            stats.forEach { (statName: String, value: Double) ->
                mainStatsMap.getOrPut(statName) { mutableMapOf() }[gameLevel] = value.toFloat()
            }
        }
        mainStatsMap.forEach { (statName: String, points) ->
            val curveId = "ARTIFACT_RANK_${rank}_MAIN_${statName}"
            result.add(StatCurveEntity(id = curveId, points = points))
        }
    }

    this.data.affix.forEach affix@{ (rankStr: String, statsMap) ->
        val rank = rankStr.toIntOrNull() ?: return@affix

        statsMap.forEach { (statName: String, rolls) ->
            val points = rolls.mapIndexed { index, value ->
                index to value.toFloat()
            }.toMap()

            val curveId = "ARTIFACT_RANK_${rank}_SUB_${statName}"
            result.add(StatCurveEntity(id = curveId, points = points))
        }
    }

    return result
}