package com.example.drillerapp.models

data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correctAnswers: List<String>
)