package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.data.model.Character
import com.nokaori.genshinaibuilder.data.model.Element
import com.nokaori.genshinaibuilder.data.model.WeaponType
import com.nokaori.genshinaibuilder.data.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CharacterRepositoryImpl : CharacterRepository {
    private val _characters = MutableStateFlow<List<Character>>(emptyList())

    init {
        _characters.value = createMockCharacters()
    }

    override fun getCharacters(): Flow<List<Character>> = _characters.asStateFlow()

    override suspend fun getCharacterById(id: Int): Character? {
        return _characters.value.find { it.id == id }
    }

    override suspend fun toggleCharacterOwnership(characterId: Int) {
        _characters.update { currentList ->
            currentList.map { character ->
                if (character.id == characterId) {
                    character.copy(isOwned = !character.isOwned)
                } else {
                    character
                }
            }
        }
    }

    private fun createMockCharacters(): List<Character> {
        return listOf(
            Character(
                id = 1,
                name = "Amber",
                element = Element.PYRO,
                weaponType = WeaponType.SWORD,
                rarity = 5,
                isOwned = false
            ),
            Character(
                id = 2,
                name = "Barbara",
                element = Element.HYDRO,
                weaponType = WeaponType.CATALYST,
                rarity = 4,
                isOwned = false
            ),
            Character(
                id = 3,
                name = "Kaeya",
                element = Element.CRYO,
                weaponType = WeaponType.SWORD,
                rarity = 4,
                isOwned = false
            ),
            Character(
                id = 4,
                name = "Lisa",
                element = Element.ELECTRO,
                weaponType = WeaponType.CATALYST,
                rarity = 4,
                isOwned = false
            ),
            Character(
                id = 5,
                name = "Razor",
                element = Element.ELECTRO,
                weaponType = WeaponType.CLAYMORE,
                rarity = 4,
                isOwned = false
            ),
            Character(
                id = 6,
                name = "Venti",
                element = Element.ANEMO,
                weaponType = WeaponType.BOW,
                rarity = 5,
                isOwned = false
            ),
            Character(
                id = 7,
                name = "Xiao",
                element = Element.ANEMO,
                weaponType = WeaponType.POLEARM,
                rarity = 5,
                isOwned = false
            ),
            Character(
                id = 8,
                name = "YunJin",
                element = Element.GEO,
                weaponType = WeaponType.POLEARM,
                rarity = 4,
                isOwned = false
            ),
            Character(
                id = 9,
                name = "Zhongli",
                element = Element.GEO,
                weaponType = WeaponType.POLEARM,
                rarity = 5,
                isOwned = false
            )
        )
    }
}