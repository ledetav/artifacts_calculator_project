package com.nokaori.genshinaibuilder.domain.model

object ArtifactRules {
    private val ELEMENTAL_DMG_STATS = listOf(
        StatType.PYRO_DAMAGE_BONUS,
        StatType.HYDRO_DAMAGE_BONUS,
        StatType.CRYO_DAMAGE_BONUS,
        StatType.ELECTRO_DAMAGE_BONUS,
        StatType.ANEMO_DAMAGE_BONUS,
        StatType.GEO_DAMAGE_BONUS,
        StatType.DENDRO_DAMAGE_BONUS
    )

    fun getAllowedMainStats(slot: ArtifactSlot): List<StatType> {
        return when (slot) {
            ArtifactSlot.FLOWER_OF_LIFE -> listOf(
                StatType.HP
            )

            ArtifactSlot.PLUME_OF_DEATH -> listOf(
                StatType.ATK
            )

            ArtifactSlot.SANDS_OF_EON -> listOf(
                StatType.HP_PERCENT,
                StatType.ATK_PERCENT,
                StatType.DEF_PERCENT,
                StatType.ENERGY_RECHARGE,
                StatType.ELEMENTAL_MASTERY
            )

            ArtifactSlot.GOBLET_OF_EONOTHEM -> listOf(
                StatType.HP_PERCENT,
                StatType.ATK_PERCENT,
                StatType.DEF_PERCENT,
                StatType.ELEMENTAL_MASTERY,
                StatType.PHYSICAL_DAMAGE_BONUS
            ) + ELEMENTAL_DMG_STATS

            ArtifactSlot.CIRCLET_OF_LOGOS -> listOf(
                StatType.HP_PERCENT,
                StatType.ATK_PERCENT,
                StatType.DEF_PERCENT,
                StatType.ELEMENTAL_MASTERY,
                StatType.CRIT_RATE,
                StatType.CRIT_DMG,
                StatType.HEALING_BONUS
            )
        }
    }
}