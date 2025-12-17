package com.nokaori.genshinaibuilder.presentation.util

import com.nokaori.genshinaibuilder.domain.model.ArtifactSlot
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.WeaponType

object YattaAssets {
    private const val BASE_URL = "https://gi.yatta.moe/assets/UI"

    // Получить иконку стихии
    fun getElementIconUrl(element: Element): String {
        val internalName = when(element) {
            Element.PYRO -> "Fire"
            Element.HYDRO -> "Water"
            Element.ANEMO -> "Wind"
            Element.ELECTRO -> "Electric"
            Element.DENDRO -> "Grass"
            Element.CRYO -> "Ice"
            Element.GEO -> "Rock"
            else -> "Unknown"
        }
        // UI_MessageIcon_Fire.png
        return "$BASE_URL/other/UI_MessageIcon_$internalName.png"
    }

    // Получить иконку типа оружия (для фильтров оружия)
    fun getWeaponTypeIconUrl(type: WeaponType): String {
        val internalName = when(type) {
            WeaponType.SWORD -> "Sword"
            WeaponType.CLAYMORE -> "Claymore"
            WeaponType.POLEARM -> "Pole"
            WeaponType.BOW -> "Bow"
            WeaponType.CATALYST -> "Catalyst"
            else -> "Unknown"
        }
        // UI_GachaTypeIcon_Sword.png
        return "$BASE_URL/UI_GachaTypeIcon_$internalName.png"
    }
    
    fun getArtifactSlotIconUrl(slot: ArtifactSlot): String {
        val iconName = when(slot) {
            ArtifactSlot.FLOWER_OF_LIFE -> "Bracer"
            ArtifactSlot.PLUME_OF_DEATH -> "Necklace"
            ArtifactSlot.SANDS_OF_EON -> "Shoes"
            ArtifactSlot.GOBLET_OF_EONOTHEM -> "Ring"
            ArtifactSlot.CIRCLET_OF_LOGOS -> "Dress"
        }
        // Ui_Icon_Equip_Bracer.png
        return "$BASE_URL/UI_Icon_Equip_$iconName.png"
    }
}