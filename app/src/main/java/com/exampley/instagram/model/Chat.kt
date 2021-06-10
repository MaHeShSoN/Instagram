package com.exampley.instagram.model

data class Chat(
    val id: String = "",
    val text: String = "",
    val fromId: String = "",
    val toId: String = "",
    val timeScpae: Long = -1
)