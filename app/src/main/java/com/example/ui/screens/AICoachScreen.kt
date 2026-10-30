package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.ui.components.formatRupee
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SurfaceBackground
import com.example.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AICoachScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isAITyping.collectAsState()
    val budget by viewModel.currentBudget.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val currentMonthKey = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    val monthTxs = transactions.filter {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.dateMillis)) == currentMonthKey && !it.isIncome
    }
    val spentTotal = monthTxs.sumOf { it.amount }
    val budgetLimit = budget?.totalLimit ?: (profile?.monthlyPocketMoney ?: 5000.0)
    val remaining = (budgetLimit - spentTotal).coerceAtLeast(0.0)

    val quickPrompts = listOf(
        "How can I save ₹500 this month?",
        "Explain SIPs simply.",
        "Help me reduce food & canteen expenses.",
        "How does education loan interest work?",
        "College students credit score (CIBIL) kaise banayein?",
        "Explain 50/30/20 rule in simple Hinglish."
    )

    LaunchedEffect(chatMessages.size, isTyping) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
    ) {
        // Money Coach Signature Header
        Surface(
            color = SurfaceBackground,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SoftMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Money Coach",
                                tint = PrimaryBrand,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Bindora AI Coach", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryText)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SoftMint)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Bindora AI", color = PrimaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("Every Rupee Has a Purpose • Personalized for college life", fontSize = 11.sp, color = SecondaryText)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.clearAIChat() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = SecondaryText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Compact Financial Context Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBackground)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Context: ${formatRupee(budgetLimit)} budget",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryDark
                        )
                        Text(
                            text = "${formatRupee(remaining)} remaining",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBrand
                        )
                    }
                }
            }
        }

        // Suggested Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceBackground)
                        .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                        .clickable { viewModel.sendMessageToAICoach(prompt) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = prompt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBrand
                    )
                }
            }
        }

        // Chat Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            items(chatMessages) { msg ->
                EditorialChatBubble(message = msg)
            }

            if (isTyping) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryBrand
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Analyzing your student budget & formulation...",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Input Field Bar
        Surface(
            color = SurfaceBackground,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = SubtleBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask in Hindi, English, or Hinglish...", fontSize = 13.sp, color = SecondaryText) },
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_coach_input_field")
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotBlank()) {
                            viewModel.sendMessageToAICoach(text)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryBrand)
                        .testTag("ai_coach_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditorialChatBubble(message: ChatMessageEntity) {
    val isUser = message.sender == "user"
    val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestampMillis))
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SoftMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Coach",
                    tint = PrimaryBrand,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.88f)
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) PrimaryBrand else SurfaceBackground
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (isUser) PrimaryBrand else SubtleBorder,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = if (isUser) Color.White else PrimaryText,
                        lineHeight = 19.sp
                    )

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { clipboardManager.setText(AnnotatedString(message.text)) }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SecondaryText, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy answer", fontSize = 10.sp, color = SecondaryText, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeStr,
                fontSize = 10.sp,
                color = SecondaryText
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PrimaryBackground)
                    .border(width = 1.dp, color = SubtleBorder, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = PrimaryDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
