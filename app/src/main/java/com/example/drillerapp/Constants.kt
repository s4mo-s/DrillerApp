package com.example.drillerapp

object Constants {

    const val TOTAL_QUESTIONS: String = "total_question"
    const val CORRECT_ANSWERS: String = "correct_answers"

    fun getQuestions(): ArrayList<QuestionModel> {
        val questionsList = ArrayList<QuestionModel>()

        val question1 = QuestionModel(1, "How much is 1 + 1?", "2", "3", "4", "6", 1)
        val question2 = QuestionModel(1, "How much is 2 + 2?", "2", "4", "8", "12", 2)
        val question3 = QuestionModel(1, "How much is 3 + 3?", "3", "5", "6", "10", 3)
        questionsList.add(question1)
        questionsList.add(question2)
        questionsList.add(question3)

        return questionsList
    }
}