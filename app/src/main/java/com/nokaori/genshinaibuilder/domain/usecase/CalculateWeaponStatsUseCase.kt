package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.Weapon
import com.nokaori.genshinaibuilder.domain.model.StatValue
import javax.inject.Inject

class CalculateWeaponStatsUseCase @Inject constructor() {
    data class CalculatedStats(
        val baseAttack: Int,
        val mainStat: Stat?
    )

    operator fun invoke(userWeapon: UserWeapon): CalculatedStats {
        return invoke(userWeapon.weapon, userWeapon.level, userWeapon.ascension)
    }

    operator fun invoke(weapon: Weapon, level: Int, ascension: Int): CalculatedStats {

        // ПОКА ЧТО ЗАГЛУШКА
        val calculatedBaseAttack = weapon.baseAttackLvl1 + (level * 5) // упр. линейная регрессия

        val calculatedMainStat = weapon.mainStat?.let {
            // Очень примитивно-грубенький рассчет стата в качестве заглушки.
            val calculatedValue = when(val value = it.value) {
                is StatValue.DoubleValue -> StatValue.DoubleValue(value.value * (level / 90.0))
                is StatValue.IntValue -> StatValue.IntValue((value.value * (level / 90.0)).toInt())
            }
            it.copy(value = calculatedValue)
        }

        return CalculatedStats(
            baseAttack = calculatedBaseAttack,
            mainStat = calculatedMainStat
        )
    }
}