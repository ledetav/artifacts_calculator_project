package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.Element
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

class FilterCharactersUseCase @Inject constructor() {

    enum class OwnershipFilter {
        ALL,
        ONLY_OWNED,
        ONLY_MISSING
    }

    private val collator = Collator.getInstance(Locale("ru")).apply {
        strength = Collator.SECONDARY
    }

    operator fun invoke(
        characters: List<Character>,
        searchQuery: String,
        selectedElements: Set<Element>,
        ownershipFilter: OwnershipFilter
    ): List<Character> {
        return characters.filter { character ->
            // 1. Поиск по имени
            val matchesSearch = searchQuery.isBlank() || 
                character.name.contains(searchQuery, ignoreCase = true)

            // 2. Фильтр по стихиям (если пусто, то показываем всех)
            val matchesElement = selectedElements.isEmpty() || 
                character.element in selectedElements

            // 3. Фильтр по наличию
            val matchesOwnership = when (ownershipFilter) {
                OwnershipFilter.ALL -> true
                OwnershipFilter.ONLY_OWNED -> character.isOwned
                OwnershipFilter.ONLY_MISSING -> !character.isOwned
            }

            matchesSearch && matchesElement && matchesOwnership
        }.sortedWith(
            // Сортировка: Сначала 5 звезд, потом 4. Внутри звездности - по алфавиту.
            compareByDescending<Character> { it.rarity }
                .thenBy(collator) { it.name }
        )
    }
}