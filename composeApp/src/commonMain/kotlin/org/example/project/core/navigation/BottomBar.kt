package org.example.project.core.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.resources.AppIcons

@Composable
fun BottomBar(
    currentRoute: Any?,
    modifier: Modifier = Modifier,
    onNavigate: (Any) -> Unit
) {
    NavigationBar(modifier = modifier) {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = getIconForItem(item),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}

@Composable
private fun getIconForItem(item: BottomNavItem): ImageVector {
    return when (item) {
        is BottomNavItem.Home -> AppIcons.home
        is BottomNavItem.Bookings -> AppIcons.bookmark
        is BottomNavItem.Profile -> AppIcons.personLarge
    }
}
