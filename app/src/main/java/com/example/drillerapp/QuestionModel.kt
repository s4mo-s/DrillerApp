package com.example.drillerapp

data class QuestionModel(
    val question: String,
    val answers: List<String>,
    val correctAnswers: List<String>
)