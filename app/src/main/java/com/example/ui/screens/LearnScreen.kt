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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademyLesson
import com.example.data.repository.AcademyRepository
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.PrimaryBackground
import com.example.ui.theme.PrimaryBrand
import com.example.ui.theme.PrimaryDark
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftBlue
import com.example.ui.theme.SoftMint
import com.example.ui.theme.SubtleBorder
import com.example.ui.theme.SuccessColor
import com.example.ui.theme.SurfaceBackground
import com.example.ui.viewmodel.FinanceViewModel

@Composable
fun LearnScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.lessonProgress.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()

    val completedCount = progressList.count { it.isCompleted }
    val totalLessons = AcademyRepository.LESSONS.size
    val completionPercent = ((completedCount.toFloat() / totalLessons.toFloat()) * 100).toInt()

    var selectedFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Pocket Money", "Investing", "Loans", "Scam Warning", "Wealth Builder")

    val filteredLessons = AcademyRepository.LESSONS.filter { lesson ->
        if (selectedFilter == "All") true
        else lesson.studentTag.contains(selectedFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Academy Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Financial Education Academy",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "14 Practical lessons designed for Indian college students",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftMint)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "$completedCount of $totalLessons",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = PrimaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { completedCount.toFloat() / totalLessons.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryBrand,
                        trackColor = PrimaryBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$completionPercent% course completed · Earn verified badges for each quiz.",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryBrand else SurfaceBackground)
                            .border(width = 1.dp, color = if (isSelected) PrimaryBrand else SubtleBorder, shape = RoundedCornerShape(10.dp))
                            .clickable { selectedFilter = cat }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else PrimaryText
                        )
                    }
                }
            }
        }

        // Lesson Cards
        items(filteredLessons) { lesson ->
            val isDone = progressList.any { it.lessonId == lesson.id && it.isCompleted }
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(16.dp))
                    .clickable { viewModel.openLesson(lesson) }
                    .testTag("lesson_card_${lesson.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isDone) SoftMint else PrimaryBackground)
                            .border(width = 1.dp, color = if (isDone) PrimaryBrand else SubtleBorder, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (isDone) SuccessColor else PrimaryBrand,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SoftBlue)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(lesson.studentTag, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryBackground)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Beginner", fontSize = 9.sp, fontWeight = FontWeight.Medium, color = SecondaryText)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(lesson.readingTime, fontSize = 10.sp, color = SecondaryText)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lesson.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = lesson.summary,
                            fontSize = 12.sp,
                            color = SecondaryText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }

    if (selectedLesson != null) {
        LessonDetailSheet(
            lesson = selectedLesson!!,
            onDismiss = { viewModel.closeLesson() },
            onSubmitQuiz = { score, total ->
                viewModel.submitLessonQuiz(selectedLesson!!.id, score, total)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailSheet(
    lesson: AcademyLesson,
    onDismiss: () -> Unit,
    onSubmitQuiz: (score: Int, total: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isQuizMode by remember { mutableStateOf(false) }

    var selectedOptionIndices by remember { mutableStateOf(mapOf<Int, Int>()) }
    var quizSubmitted by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lesson.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = PrimaryText,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isQuizMode) {
                // LESSON READING VIEW
                Text(
                    text = lesson.content,
                    fontSize = 13.sp,
                    color = PrimaryText,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Real Student Example
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftMint),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = PrimaryBrand.copy(alpha = 0.2f), shape = RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("👨‍🎓 College Case Study", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryDark)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(lesson.studentExample, fontSize = 12.sp, lineHeight = 18.sp, color = PrimaryText)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Takeaways
                Text("📌 Key Takeaways:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryText)
                Spacer(modifier = Modifier.height(6.dp))
                lesson.keyTakeaways.forEach { takeaway ->
                    Text("• $takeaway", fontSize = 12.sp, lineHeight = 17.sp, color = SecondaryText, modifier = Modifier.padding(vertical = 2.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { isQuizMode = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("take_quiz_button")
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Practice Quiz (${lesson.quiz.size} Questions)", fontWeight = FontWeight.Bold)
                }
            } else {
                // QUIZ VIEW
                Text("Interactive Knowledge Check", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryDark)
                Spacer(modifier = Modifier.height(12.dp))

                lesson.quiz.forEachIndexed { qIndex, q ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(width = 1.dp, color = SubtleBorder, shape = RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Q${qIndex + 1}: ${q.question}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryText)
                            Spacer(modifier = Modifier.height(8.dp))

                            q.options.forEachIndexed { optIndex, optText ->
                                val isSelected = selectedOptionIndices[qIndex] == optIndex
                                val isCorrect = q.correctIndex == optIndex
                                val optionBg = when {
                                    quizSubmitted && isCorrect -> SoftMint
                                    quizSubmitted && isSelected && !isCorrect -> ErrorBackground
                                    isSelected -> SoftBlue
                                    else -> SurfaceBackground
                                }
                                val optionBorder = when {
                                    quizSubmitted && isCorrect -> PrimaryBrand
                                    quizSubmitted && isSelected && !isCorrect -> ErrorColor
                                    isSelected -> SecondaryBlue
                                    else -> SubtleBorder
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(optionBg)
                                        .border(width = 1.dp, color = optionBorder, shape = RoundedCornerShape(10.dp))
                                        .clickable(enabled = !quizSubmitted) {
                                            selectedOptionIndices = selectedOptionIndices + (qIndex to optIndex)
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            if (!quizSubmitted) {
                                                selectedOptionIndices = selectedOptionIndices + (qIndex to optIndex)
                                            }
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrand)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(optText, fontSize = 12.sp, color = PrimaryText, modifier = Modifier.weight(1f))
                                }
                            }

                            if (quizSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                val userChoice = selectedOptionIndices[qIndex]
                                val userRight = userChoice == q.correctIndex
                                Text(
                                    text = if (userRight) "✅ Correct! " + q.explanation else "❌ Incorrect. Correct: ${q.options[q.correctIndex]}. " + q.explanation,
                                    fontSize = 11.sp,
                                    color = if (userRight) SuccessColor else ErrorColor,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!quizSubmitted) {
                    Button(
                        onClick = {
                            var score = 0
                            lesson.quiz.forEachIndexed { index, quizQuestion ->
                                if (selectedOptionIndices[index] == quizQuestion.correctIndex) {
                                    score++
                                }
                            }
                            quizSubmitted = true
                            onSubmitQuiz(score, lesson.quiz.size)
                        },
                        enabled = selectedOptionIndices.size == lesson.quiz.size,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Submit Answers", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrand),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Finish & Return", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
