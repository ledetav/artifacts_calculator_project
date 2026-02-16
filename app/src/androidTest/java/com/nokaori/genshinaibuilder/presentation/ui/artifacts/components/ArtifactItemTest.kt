package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nokaori.genshinaibuilder.domain.model.Artifact
import com.nokaori.genshinaibuilder.domain.model.ArtifactSet
import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Rarity
import com.nokaori.genshinaibuilder.domain.model.Stat
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.domain.model.StatValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArtifactItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testArtifactItemDisplaysName() {
        val artifact = createTestArtifact("Test Artifact")
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("Test Artifact").assertIsDisplayed()
    }

    @Test
    fun testArtifactItemDisplaysSetAndLevel() {
        val artifact = createTestArtifact("Test Artifact", level = 20)
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("Test Set, Lv. 20").assertIsDisplayed()
    }

    @Test
    fun testArtifactItemDisplaysRarity() {
        val artifact = createTestArtifact("Test Artifact", rarity = Rarity.FIVE_STAR)
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("⭐⭐⭐⭐⭐").assertIsDisplayed()
    }

    @Test
    fun testArtifactItemDisplaysSlot() {
        val artifact = createTestArtifact("Test Artifact")
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("Цветок жизни").assertIsDisplayed()
    }

    @Test
    fun testArtifactItemDisplaysMainStat() {
        val artifact = createTestArtifact("Test Artifact")
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("HP 100").assertIsDisplayed()
    }

    @Test
    fun testArtifactItemDisplaysPercentageStat() {
        val mainStat = Stat(
            type = StatType.HP_PERCENT,
            value = StatValue.DoubleValue(10.5)
        )
        val artifact = createTestArtifact("Test Artifact", mainStat = mainStat)
        composeTestRule.setContent {
            ArtifactItem(artifact = artifact)
        }

        composeTestRule.onNodeWithText("HP 10.5%").assertIsDisplayed()
    }

    private fun createTestArtifact(
        name: String,
        level: Int = 20,
        rarity: Rarity = Rarity.FIVE_STAR,
        mainStat: Stat = Stat(
            type = StatType.HP,
            value = StatValue.IntValue(100)
        )
    ): Artifact {
        return Artifact(
            id = 1,
            artifactName = name,
            setName = "Test Set",
            slot = ArtifactSlot.FLOWER,
            level = level,
            rarity = rarity,
            mainStat = mainStat,
            subStats = emptyList()
        )
    }
}
