package com.nokaori.genshinaibuilder.presentation.ui.characters.data

import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase

data class CharacterFilterState(
    val selectedElements: Set<Element> = emptySet(),
    val ownershipFilter: FilterCharactersUseCase.OwnershipFilter = FilterCharactersUseCase.OwnershipFilter.ALL
)