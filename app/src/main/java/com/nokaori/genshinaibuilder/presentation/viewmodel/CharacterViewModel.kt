package com.nokaori.genshinaibuilder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
import com.nokaori.genshinaibuilder.domain.repository.ThemeRepository
import com.nokaori.genshinaibuilder.domain.usecase.FilterCharactersUseCase
import com.nokaori.genshinaibuilder.presentation.ui.characters.data.CharacterFilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val filterCharactersUseCase: FilterCharactersUseCase,
    private val themeRepository: ThemeRepository
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

    val hasActiveFilters: StateFlow<Boolean> = _filterState.map {
        it != CharacterFilterState()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Основной список
    val characters: StateFlow<List<Character>> = combine(
        characterRepository.getCharacters(),
        _searchQuery,
        _filterState,
        themeRepository.appLanguage
    ) { list, query, filter, _ ->
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
        if (_isFilterDialogShown.value) {
            _draftFilterState.value = _filterState.value
        } else {
            val defaultState = CharacterFilterState()
            _filterState.value = defaultState
            _draftFilterState.value = defaultState
        }
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