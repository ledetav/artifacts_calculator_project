package com.nokaori.genshinaibuilder.presentation.ui.weapons.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nokaori.genshinaibuilder.R
import com.nokaori.genshinaibuilder.domain.model.UserWeapon
import com.nokaori.genshinaibuilder.presentation.ui.mappers.toDisplayName

@Composable
fun UserWeaponItem(userWeapon: UserWeapon){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${userWeapon.weapon.name} [R${userWeapon.refinement}]",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(
                    id = R.string.weapon_level,
                    userWeapon.level
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "⭐".repeat(userWeapon.weapon.rarity.stars),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userWeapon.weapon.type.toDisplayName(),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${stringResource(R.string.stat_type_atk)}: ${userWeapon.weapon.baseAttackLvl1}",
                style = MaterialTheme.typography.bodyMedium
            )

            userWeapon.weapon.mainStat?.let { stat ->
                val valueText = when (val v = stat.value) {
                    is com.nokaori.genshinaibuilder.domain.model.StatValue.IntValue -> v.value.toString()
                    is com.nokaori.genshinaibuilder.domain.model.StatValue.DoubleValue -> "%.1f".format(v.value)
                }
                val percentSign = if (stat.type.isPercentage) "%" else ""
                Text(
                    text = "${stat.type.toDisplayName(showPercentSign = false)}: $valueText$percentSign",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}