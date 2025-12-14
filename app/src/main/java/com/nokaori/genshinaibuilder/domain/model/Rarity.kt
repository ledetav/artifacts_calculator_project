package com.nokaori.genshinaibuilder.domain.model

enum class Rarity(val stars: Int) {
    UNKNOWN(0),
    ONE_STAR(1),
    TWO_STARS(2),
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5);

    companion object {
        fun fromInt(stars: Int?): Rarity {
            return entries.find { it.stars == stars } ?: UNKNOWN
        }
    }
}