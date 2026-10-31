package co.edu.uniquindio.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NetworkStatusBanner(isOnline: Boolean) {
    val backgroundColor = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
    val text = if (isOnline) "Conectado (Online)" else "Sin conexión a Internet (Offline)"
    val icon = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}