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
                text = "⭐".repeat(userWeapon.weapon.rarity),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userWeapon.weapon.type.toDisplayName(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}