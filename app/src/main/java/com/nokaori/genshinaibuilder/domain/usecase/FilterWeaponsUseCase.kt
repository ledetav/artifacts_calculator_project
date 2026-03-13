package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class FilterWeaponsUseCase @Inject constructor() {
    private val collator = Collator.getInstance(Locale("ru")).apply {
        strength = Collator.SECONDARY
    }
    operator fun invoke(
        weapons: List<UserWeapon>,
        searchQuery: String,
        selectedWeaponTypes: Set<WeaponType>,
        levelRange: ClosedFloatingPointRange<Float>,
        selectedMainStat: StatType?
    ): List<UserWeapon> {
        return weapons.filter { userWeapon ->
            // 1. Поиск по имени
            val matchSearch = searchQuery.isBlank() ||
                    userWeapon.weapon.name.contains(searchQuery, ignoreCase = true)

            // 2. Фильтр по типу оружия
            val matchType = selectedWeaponTypes.isEmpty() ||
                    userWeapon.weapon.type in selectedWeaponTypes

            // 3. Фильтр по уровню
            val matchLevel = userWeapon.level.toFloat() in levelRange

            // 4. Фильтр по главному стату (если есть у оружия)
            val matchStat = selectedMainStat == null ||
                    userWeapon.weapon.mainStat?.type == selectedMainStat

            matchSearch && matchType && matchLevel && matchStat
        }.sortedWith(
            // Сортировка: Сначала по звездам (убывание), потом по уровню (убывание), потом по имени
            compareByDescending<UserWeapon> { it.weapon.rarity.stars }
                .thenByDescending { it.level }
                .thenBy(collator) { it.weapon.name }
        )
    }
}