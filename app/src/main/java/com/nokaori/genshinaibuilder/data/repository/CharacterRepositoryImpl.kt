package com.nokaori.genshinaibuilder.data.repository

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import com.nokaori.genshinaibuilder.domain.repository.CharacterRepository
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
                name = "Hu Tao",
                element = Element.PYRO,
                weaponType = WeaponType.POLEARM,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Hutao.png", // Заглушка
                isOwned = true
            ),
            Character(
                id = 2,
                name = "Yelan",
                element = Element.HYDRO,
                weaponType = WeaponType.BOW,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Yelan.png",
                isOwned = true
            ),
            Character(
                id = 3,
                name = "Nahida",
                element = Element.DENDRO,
                weaponType = WeaponType.CATALYST,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Nahida.png",
                isOwned = false // Для теста фильтра "Only Owned"
            ),
            Character(
                id = 4,
                name = "Raiden Shogun",
                element = Element.ELECTRO,
                weaponType = WeaponType.POLEARM,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Shougun.png",
                isOwned = true
            ),
            Character(
                id = 5,
                name = "Kazuha",
                element = Element.ANEMO,
                weaponType = WeaponType.SWORD,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Kazuha.png",
                isOwned = true
            ),
             Character(
                id = 6,
                name = "Zhongli",
                element = Element.GEO,
                weaponType = WeaponType.POLEARM,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Zhongli.png",
                isOwned = false
            ),
            Character(
                id = 7,
                name = "Ayaka",
                element = Element.CRYO,
                weaponType = WeaponType.SWORD,
                rarity = 5,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Ayaka.png",
                isOwned = true
            ),
             Character(
                id = 8,
                name = "Xiangling",
                element = Element.PYRO,
                weaponType = WeaponType.POLEARM,
                rarity = 4,
                iconUrl = "https://gi.yatta.moe/assets/UI/UI_AvatarIcon_Xiangling.png",
                isOwned = true
            )
        )
    }
}