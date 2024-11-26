package com.courses.client.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
	val id: Int,
	val userId: Int,
	val title: String,
	val body: String,
)