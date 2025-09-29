package com.nokaori.genshinaibuilder.data

data class Artifact(
    val id: Int = 0, // по умолчанию для нового артефакта
    val slot: ArtifactSlot, // цветок, перо, часы, кубок, корона
    val setName: String, // название сета артефактов
    val level: Int, // уроень прокачки
    val mainStat: ArtifactStat,
    val subStats: List<ArtifactStat>
)

data class ArtifactStat(
    val type: StatType, // тип хар-ки
    val value: Double
)

enum class ArtifactSlot{
    FLOWER_OF_LIFE,
    PLUME_OF_DEATH,
    SANDS_OF_EON,
    GOBLET_OF_EONOTHEM,
    CIRCLET_OF_LOGOS
}

enum class StatType(val displayName: String){
    // Плоские
    HP("HP"),
    ATK("Сила атаки"),
    DEF("Защита"),

    // Проценты
    HP_PERCENT("HP %"),
    ATK_PERCENT("Сила атаки %"),
    DEF_PERCENT("Защита %"),

    // Крит
    CRIT_RATE("Шанс крит. попадания %"),
    CRIT_DMG("Крит. урон %"),

    // ВЭ, МС
    ENERGY_RECHARGE("Восст. энергии %"),
    ELEMENTAL_MASTERY("Мастерство стихий"),

    // Урон
    PYRO_DAMAGE_BONUS("Бонус Пиро урона %"),
    HYDRO_DAMAGE_BONUS("Бонус Гидро урона %"),
    DENDRO_DAMAGE_BONUS("Бонус Дендро урона %"),
    ELECTRO_DAMAGE_BONUS("Бонус Электро урона %"),
    ANEMO_DAMAGE_BONUS("Бонус Анемо урона %"),
    CRYO_DAMAGE_BONUS("Бонус Крио урона %"),
    GEO_DAMAGE_BONUS("Бонус Гео урона %"),
    PHYSICAL_DAMAGE_BONUS("Бонус физ. урона %"),

    // Лечение
    HEALING_BONUS("Бонус лечения %")
}