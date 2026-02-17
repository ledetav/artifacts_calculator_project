package com.nokaori.genshinaibuilder.presentation.ui.artifacts.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.Element
import com.nokaori.genshinaibuilder.domain.model.StatType
import com.nokaori.genshinaibuilder.presentation.util.YattaAssets

@Composable
fun StatIcon(
    statType: StatType,
    modifier: Modifier = Modifier.size(16.dp) 
) {
    val element = statType.getAssociatedElement()
    
    if (element != null) {
        AsyncImage(
            model = YattaAssets.getElementIconUrl(element),
            contentDescription = statType.name,
            modifier = modifier
        )
    } else {
        val iconRes = statType.getIconRes()
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = statType.name,
                modifier = modifier
            )
        }
    }
}

private fun StatType.getAssociatedElement(): Element? = when(this) {
    StatType.PYRO_DAMAGE_BONUS -> Element.PYRO
    StatType.HYDRO_DAMAGE_BONUS -> Element.HYDRO
    StatType.DENDRO_DAMAGE_BONUS -> Element.DENDRO
    StatType.ELECTRO_DAMAGE_BONUS -> Element.ELECTRO
    StatType.ANEMO_DAMAGE_BONUS -> Element.ANEMO
    StatType.CRYO_DAMAGE_BONUS -> Element.CRYO
    StatType.GEO_DAMAGE_BONUS -> Element.GEO
    else -> null
}

private fun StatType.getIconRes(): Int? = when(this) {
    StatType.HP, StatType.HP_PERCENT -> R.drawable.ic_stat_hp
    StatType.ATK, StatType.ATK_PERCENT -> R.drawable.ic_stat_atk
    StatType.DEF, StatType.DEF_PERCENT -> R.drawable.ic_stat_def
    StatType.ELEMENTAL_MASTERY -> R.drawable.ic_stat_ele_mas
    StatType.ENERGY_RECHARGE -> R.drawable.ic_stat_ener_rech
    StatType.CRIT_RATE -> R.drawable.ic_stat_crit_rate
    StatType.CRIT_DMG -> R.drawable.ic_stat_crit_dmg
    StatType.HEALING_BONUS -> R.drawable.ic_stat_healing
    StatType.PHYSICAL_DAMAGE_BONUS -> R.drawable.ic_stat_physical
    else -> null
}