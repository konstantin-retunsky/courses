package com.courses.designsystem.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CustomButton(
	onClick: () -> Unit,
	text: String,
) {
	Button(onClick = onClick) {
		Text(text = text)
	}
}
