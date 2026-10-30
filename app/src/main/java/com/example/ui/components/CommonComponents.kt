package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FinanceConstants
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.WarningColor
import com.example.ui.viewmodel.MainScreen
import java.text.DecimalFormat
import java.util.Calendar

val CurrencyFormatter = DecimalFormat("#,##0")

fun formatRupee(amount: Double): String {
    return "₹" + CurrencyFormatter.format(amount)
}

fun getTimeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
fun AppTopHeader(
    studentName: String,
    college: String,
    isHosteller: Boolean,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = getTimeBasedGreeting()
    val firstName = studentName.trim().split(" ").firstOrNull()?.ifBlank { "Student" } ?: "Student"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = SubtleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SoftMint)
                    .border(width = 1.dp, color = PrimaryBrand.copy(alpha = 0.2f), shape = CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (studentName.isNotBlank()) studentName.take(1).uppercase() else "S",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDark,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$greeting, $firstName",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isHosteller) Color(0xFFEAF0FC) else Color(0xFFFFF3DF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isHosteller) "Hostel" else "Day Scholar",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isHosteller) Color(0xFF4267A9) else WarningColor
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bindora_emblem),
                        contentDescription = "Bindora",
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Bindora",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• Every Rupee Has a Purpose",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Notification Bell with unread indicator
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = WarningColor,
                        modifier = Modifier.size(7.dp)
                    )
                }
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("top_bar_notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = PrimaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentScreen: MainScreen,
    onScreenSelected: (MainScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = modifier.border(width = 1.dp, color = SubtleBorder)
    ) {
        val items = listOf(
            Triple(MainScreen.HOME, "Home", Icons.Default.Home),
            Triple(MainScreen.TRANSACTIONS_BUDGET, "Budget", Icons.Default.AccountBalanceWallet),
            Triple(MainScreen.LEARN, "Learn", Icons.Default.MenuBook),
            Triple(MainScreen.AI_COACH, "Money Coach", Icons.Default.AutoAwesome),
            Triple(MainScreen.TOOLS_SIM, "Tools & Sim", Icons.Default.ShowChart)
        )

        items.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryDark,
                    indicatorColor = SoftMint,
                    unselectedIconColor = SecondaryText,
                    selectedTextColor = PrimaryDark,
                    unselectedTextColor = SecondaryText
                ),
                modifier = Modifier.testTag("nav_item_${label.lowercase().replace(" ", "_")}")
            )
        }
    }
}

@Composable
fun CategoryAvatar(
    categoryName: String,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    val meta = FinanceConstants.getCategoryMeta(categoryName)
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(meta.color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = meta.icon,
            contentDescription = categoryName,
            tint = meta.color,
            modifier = Modifier.size((size * 0.52).dp)
        )
    }
}
