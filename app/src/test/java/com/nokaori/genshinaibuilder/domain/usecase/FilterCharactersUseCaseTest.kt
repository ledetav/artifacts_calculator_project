package com.nokaori.genshinaibuilder.domain.usecase

import com.nokaori.genshinaibuilder.domain.model.Character
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.WeaponType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterCharactersUseCaseTest {
    private lateinit var useCase: FilterCharactersUseCase
    private lateinit var testCharacters: List<Character>

    @Before
    fun setup() {
        useCase = FilterCharactersUseCase()
        testCharacters = listOf(
            Character(
                id = 1,
                name = "Hu Tao",
                element = Element.PYRO,
                weaponType = WeaponType.POLEARM,
                rarity = Rarity.FIVE_STARS,
                iconUrl = "",
                isOwned = true,
                baseHp = 1000f,
                baseAtk = 100f,
                baseDef = 50f,
                ascensionStatType = StatType.CRIT_DMG,
                curveId = "test"
            ),
            Character(
                id = 2,
                name = "Fischl",
                element = Element.ELECTRO,
                weaponType = WeaponType.BOW,
                rarity = Rarity.FOUR_STARS,
                iconUrl = "",
                isOwned = true,
                baseHp = 800f,
                baseAtk = 90f,
                baseDef = 45f,
                ascensionStatType = StatType.ATK_PERCENT,
                curveId = "test"
            ),
            Character(
                id = 3,
                name = "Ayaka",
                element = Element.CRYO,
                weaponType = WeaponType.SWORD,
                rarity = Rarity.FIVE_STARS,
                iconUrl = "",
                isOwned = false,
                baseHp = 900f,
                baseAtk = 95f,
                baseDef = 55f,
                ascensionStatType = StatType.CRIT_DMG,
                curveId = "test"
            )
        )
    }

    @Test
    fun invoke_withEmptySearchQuery_returnsAllCharacters() {
        val result = useCase(
            testCharacters,
            "",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals(3, result.size)
    }

    @Test
    fun invoke_withSearchQuery_filtersCharacters() {
        val result = useCase(
            testCharacters,
            "Hu",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals(1, result.size)
        assertEquals("Hu Tao", result.first().name)
    }

    @Test
    fun invoke_withSelectedElements_filtersByElement() {
        val result = useCase(
            testCharacters,
            "",
            setOf(Element.PYRO, Element.CRYO),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals(2, result.size)
    }

    @Test
    fun invoke_withOwnershipFilterOwnedOnly_returnsOwnedCharacters() {
        val result = useCase(
            testCharacters,
            "",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ONLY_OWNED
        )
        assertEquals(2, result.size)
    }

    @Test
    fun invoke_withOwnershipFilterMissingOnly_returnsMissingCharacters() {
        val result = useCase(
            testCharacters,
            "",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ONLY_MISSING
        )
        assertEquals(1, result.size)
        assertEquals("Ayaka", result.first().name)
    }

    @Test
    fun invoke_withMultipleFilters_appliesAllFilters() {
        val result = useCase(
            testCharacters,
            "a",
            setOf(Element.CRYO),
            FilterCharactersUseCase.OwnershipFilter.ONLY_MISSING
        )
        assertEquals(1, result.size)
        assertEquals("Ayaka", result.first().name)
    }

    @Test
    fun invoke_sortsByRarityDescendingThenByName() {
        val result = useCase(
            testCharacters,
            "",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals("Ayaka", result[0].name)
        assertEquals("Hu Tao", result[1].name)
        assertEquals("Fischl", result[2].name)
    }

    @Test
    fun invoke_withNoMatches_returnsEmptyList() {
        val result = useCase(
            testCharacters,
            "NonExistent",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals(0, result.size)
    }

    @Test
    fun invoke_caseInsensitiveSearch() {
        val result = useCase(
            testCharacters,
            "FISCHL",
            emptySet(),
            FilterCharactersUseCase.OwnershipFilter.ALL
        )
        assertEquals(1, result.size)
        assertEquals("Fischl", result.first().name)
    }
}
