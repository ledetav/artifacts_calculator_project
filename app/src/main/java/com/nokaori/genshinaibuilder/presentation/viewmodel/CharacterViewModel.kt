package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.presentation.ui.characters.data.CharacterFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val filterCharactersUseCase: FilterCharactersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow(CharacterFilterState())
    // Состояние для диалога (черновик)
    private val _draftFilterState = MutableStateFlow(CharacterFilterState())
    val draftFilterState: StateFlow<CharacterFilterState> = _draftFilterState.asStateFlow()

    private val _isFilterDialogShown = MutableStateFlow(false)
    val isFilterDialogShown: StateFlow<Boolean> = _isFilterDialogShown.asStateFlow()

    val areFiltersChanged: StateFlow<Boolean> = combine(_filterState, _draftFilterState) { active, draft ->
        active != draft
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    // Основной список
    val characters: StateFlow<List<Character>> = combine(
        characterRepository.getCharacters(),
        _searchQuery,
        _filterState
    ) { list, query, filter ->
        filterCharactersUseCase(
            characters = list,
            searchQuery = query,
            selectedElements = filter.selectedElements,
            ownershipFilter = filter.ownershipFilter
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCharacterClicked(characterId: Int) {
        viewModelScope.launch {
            characterRepository.toggleCharacterOwnership(characterId)
        }
    }

    // --- Filter Dialog Logic ---

    fun onFilterIconClicked() {
        _draftFilterState.value = _filterState.value
        _isFilterDialogShown.value = true
    }

    fun onFilterDialogDismiss() {
        _isFilterDialogShown.value = false
    }

    fun onApplyFilters() {
        _filterState.value = _draftFilterState.value
        _isFilterDialogShown.value = false
    }

    fun onResetFilters() {
        _draftFilterState.value = CharacterFilterState()
    }

    fun onElementSelected(element: Element) {
        _draftFilterState.update { current ->
            val newElements = if (element in current.selectedElements) {
                current.selectedElements - element
            } else {
                current.selectedElements + element
            }
            current.copy(selectedElements = newElements)
        }
    }

    fun onOwnershipFilterChanged(option: FilterCharactersUseCase.OwnershipFilter) {
        _draftFilterState.update { it.copy(ownershipFilter = option) }
    }
}