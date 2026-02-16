package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.CharacterPromotion
import com.nokaori.genshinaibuilder.domain.model.CharacterStatsResult
import com.nokaori.genshinaibuilder.domain.model.StatCurve
import com.nokaori.genshinaibuilder.domain.model.UserCharacter
import javax.inject.Inject

class CalculateCharacterStatsUseCase @Inject constructor() {

    operator fun invoke(
        character: Character,
        userCharacter: UserCharacter?,
        curve: StatCurve?,
        promotions: List<CharacterPromotion>
    ): CharacterStatsResult {

        val level = userCharacter?.level ?: 1
        val ascension = userCharacter?.ascension ?: 0

        val curveMultiplier = curve?.points?.get(level) ?: 1.0f

        val promo = promotions.find { it.ascensionLevel == ascension }
        val bonusHp = promo?.addHp ?: 0f
        val bonusAtk = promo?.addAtk ?: 0f
        val bonusDef = promo?.addDef ?: 0f
        val bonusSpecial = promo?.ascensionStatValue ?: 0f

        val finalHp = (character.baseHp * curveMultiplier) + bonusHp
        val finalAtk = (character.baseAtk * curveMultiplier) + bonusAtk
        val finalDef = (character.baseDef * curveMultiplier) + bonusDef

        return CharacterStatsResult(
            maxHp = finalHp,
            atk = finalAtk,
            def = finalDef,
            ascensionStatType = character.ascensionStatType,
            ascensionStatValue = bonusSpecial
        )
    }
}