package com.example.mobilefinalproject.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun Header(text: String, modifier: Modifier = Modifier) {
  Text(
    text = text,
    modifier = modifier,
    textAlign = TextAlign.Left,
    fontWeight = FontWeight.Bold,
    letterSpacing = (-0.5).sp,
    style = MaterialTheme.typography.headlineLarge
  )
}