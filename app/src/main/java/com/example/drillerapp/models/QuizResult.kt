package com.example.drillerapp.models

data class QuizResult(
    val quizName: String,
    val score: Int,
    val totalQuestions: Int,
    val quizJson: String // Store the JSON for retaking the quiz
)