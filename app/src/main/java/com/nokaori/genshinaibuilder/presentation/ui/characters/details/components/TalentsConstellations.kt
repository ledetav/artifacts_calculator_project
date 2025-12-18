package com.nokaori.genshinaibuilder.presentation.ui.characters.details.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nokaori.genshinaibuilder.domain.model.CharacterConstellation
import com.nokaori.genshinaibuilder.domain.model.CharacterTalent

@Composable
fun TalentsList(
    talents: List<CharacterTalent>,
    userLevels: List<Int>? // null if unowned
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(talents) { talent ->
            // TODO: Логика определения открыт талант или нет на основе userLevels
            TalentItem(talent, isLocked = false, level = 1)
        }
    }
}

@Composable
fun TalentItem(talent: CharacterTalent, isLocked: Boolean, level: Int) {
    val alpha = if (isLocked) 0.5f else 1f
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(alpha),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(talent.iconUrl).build(),
                contentDescription = null,
                modifier = Modifier.size(48.dp).padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = talent.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Lvl $level") },
                        enabled = !isLocked
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = talent.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ConstellationsList(
    constellations: List<CharacterConstellation>,
    unlockedCount: Int // 0 if unowned
) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(constellations) { cons ->
            val isUnlocked = cons.order <= unlockedCount
            ConstellationItem(cons, isUnlocked)
        }
    }
}

@Composable
fun ConstellationItem(cons: CharacterConstellation, isUnlocked: Boolean) {
    val alpha = if (isUnlocked) 1f else 0.4f
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(alpha),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(cons.iconUrl).build(),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = cons.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = cons.description, style = MaterialTheme.typography.bodySmall, maxLines = if(isUnlocked) 10 else 2)
            }
        }
    }
}