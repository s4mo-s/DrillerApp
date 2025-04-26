package com.example.drillerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.example.drillerapp.databinding.ActivityQuestionsBinding
import org.json.JSONObject
import androidx.core.view.isVisible

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuestionsBinding
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<QuestionModel>? = null
    private var mSelectedAnswers: MutableList<String> = mutableListOf()
    private var mCorrectAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Determine the source of the quiz data
        val source = intent.getStringExtra("source")
        if (source == "json") {
            val quizJsonString = intent.getStringExtra("quizData")
            if (quizJsonString != null) {
                mQuestionsList = parseQuestionsFromJson(quizJsonString)
            } else {
                Toast.makeText(this, "No quiz data found!", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else if (source == "db") {
            mQuestionsList = loadQuestionsFromDatabase()
            if (mQuestionsList.isNullOrEmpty()) {
                Toast.makeText(this, "No quiz data found in the database!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        setQuestion()

        if (binding.btnSubmit.text == "SUBMIT") {
            binding.tvAnswerOne.setOnClickListener(this)
            binding.tvAnswerTwo.setOnClickListener(this)
            binding.tvAnswerThree.setOnClickListener(this)
            binding.tvAnswerFour.setOnClickListener(this)
            binding.tvAnswerFive.setOnClickListener(this)
            binding.tvAnswerSix.setOnClickListener(this)
        }
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun parseQuestionsFromJson(jsonString: String): ArrayList<QuestionModel> {
        val questionsList = ArrayList<QuestionModel>()
        try {
            val jsonObject = JSONObject(jsonString)
            val questionsArray = jsonObject.getJSONArray("questions")

            for (i in 0 until questionsArray.length()) {
                val questionObject = questionsArray.getJSONObject(i)

                val answersArray = questionObject.getJSONArray("answers")
                val answers = mutableListOf<String>()
                for (j in 0 until answersArray.length()) {
                    answers.add(answersArray.getString(j))
                }

                val correctAnswersArray = questionObject.getJSONArray("correctAnswers")
                val correctAnswers = mutableListOf<String>()
                for (j in 0 until correctAnswersArray.length()) {
                    correctAnswers.add(correctAnswersArray.getString(j))
                }

                val question = QuestionModel(
                    question = questionObject.getString("question"),
                    answers = answers,
                    correctAnswers = correctAnswers
                )
                questionsList.add(question)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing quiz data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return questionsList
    }

    private fun loadQuestionsFromDatabase(): ArrayList<QuestionModel> {
        val questionsList = ArrayList<QuestionModel>()
        // TODO: Implement database logic here
        
        return questionsList
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        val question = mQuestionsList!![mCurrentPosition - 1]

        defaultAnswersView()

        binding.btnSubmit.text = "SUBMIT"
        binding.progressBar.progress = mCurrentPosition
        binding.progressBar.max = mQuestionsList!!.size
        binding.tvProgress.text = "$mCurrentPosition / ${binding.progressBar.max}"

        binding.tvQuestion.text = question.question

        // Dynamically set the answers
        val answers = listOf(
            binding.tvAnswerOne,
            binding.tvAnswerTwo,
            binding.tvAnswerThree,
            binding.tvAnswerFour,
            binding.tvAnswerFive,
            binding.tvAnswerSix
        )

        // Show only the required number of answers
        for (i in answers.indices) {
            if (i < question.answers.size) {
                answers[i].visibility = View.VISIBLE
                answers[i].text = question.answers[i]
            } else {
                answers[i].visibility = View.GONE
            }
        }
    }

    private fun defaultAnswersView() {
        val answers = listOf(
            binding.tvAnswerOne,
            binding.tvAnswerTwo,
            binding.tvAnswerThree,
            binding.tvAnswerFour,
            binding.tvAnswerFive,
            binding.tvAnswerSix
        )

        for (answer in answers) {
            if (answer.isVisible) { // Only reset visible answers
                answer.setTextColor("#7A8000".toColorInt())
                answer.typeface = android.graphics.Typeface.DEFAULT
                answer.background = ContextCompat.getDrawable(this, R.drawable.default_answer_border)
            }
        }
    }

    private fun selectedAnswerView(tv: TextView, selectedAnswer: String) {
        if (binding.btnSubmit.text != "SUBMIT") {
            return
        }

        if (mSelectedAnswers.contains(selectedAnswer)) {
            // Deselect the answer
            mSelectedAnswers.remove(selectedAnswer)
            tv.setTextColor("#7A8000".toColorInt())
            tv.typeface = android.graphics.Typeface.DEFAULT
            tv.background = ContextCompat.getDrawable(this, R.drawable.default_answer_border)
        } else {
            // Select the answer
            mSelectedAnswers.add(selectedAnswer)
            tv.setTextColor("#363A00".toColorInt())
            tv.setTypeface(tv.typeface, android.graphics.Typeface.BOLD)
            tv.background = ContextCompat.getDrawable(this, R.drawable.selected_answer_border)
        }
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> binding.tvAnswerOne.background = ContextCompat.getDrawable(this, drawableView)
            2 -> binding.tvAnswerTwo.background = ContextCompat.getDrawable(this, drawableView)
            3 -> binding.tvAnswerThree.background = ContextCompat.getDrawable(this, drawableView)
            4 -> binding.tvAnswerFour.background = ContextCompat.getDrawable(this, drawableView)
            5 -> binding.tvAnswerFive.background = ContextCompat.getDrawable(this, drawableView)
            6 -> binding.tvAnswerSix.background = ContextCompat.getDrawable(this, drawableView)
        }
    }

    override fun onClick(v: View?) {
        val question = mQuestionsList?.get(mCurrentPosition - 1)

        when (v?.id) {
            R.id.tv_answer_one -> selectedAnswerView(binding.tvAnswerOne, question?.answers?.get(0) ?: "")
            R.id.tv_answer_two -> selectedAnswerView(binding.tvAnswerTwo, question?.answers?.get(1) ?: "")
            R.id.tv_answer_three -> selectedAnswerView(binding.tvAnswerThree, question?.answers?.get(2) ?: "")
            R.id.tv_answer_four -> selectedAnswerView(binding.tvAnswerFour, question?.answers?.get(3) ?: "")
            R.id.tv_answer_five -> selectedAnswerView(binding.tvAnswerFive, question?.answers?.get(4) ?: "")
            R.id.tv_answer_six -> selectedAnswerView(binding.tvAnswerSix, question?.answers?.get(5) ?: "")
            R.id.btn_submit -> submit()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun submit() {
        val question = mQuestionsList?.get(mCurrentPosition - 1)

        if (binding.btnSubmit.text == "SUBMIT") {
            if (mSelectedAnswers.isEmpty()) {
                // No options selected
                Toast.makeText(this, "Please select at least one answer!", Toast.LENGTH_SHORT).show()
                return
            }

            // Ensure the question is not null
            if (question == null) {
                Toast.makeText(this, "Error: Question data is missing!", Toast.LENGTH_SHORT).show()
                return
            }

            // Check if the selected answers match the correct answers
            val isCorrect = question.correctAnswers.containsAll(mSelectedAnswers) &&
                    mSelectedAnswers.containsAll(question.correctAnswers)

            if (isCorrect) {
                mCorrectAnswers++
            }

            // Highlight all correct answers
            question.correctAnswers.forEach { correctAnswer ->
                val correctAnswerPosition = question.answers.indexOf(correctAnswer) + 1
                answerView(correctAnswerPosition, R.drawable.correct_answer_border)
            }

            // Highlight wrong answers
            mSelectedAnswers.forEach { selectedAnswer ->
                if (!question.correctAnswers.contains(selectedAnswer)) {
                    val wrongAnswerPosition = question.answers.indexOf(selectedAnswer) + 1
                    answerView(wrongAnswerPosition, R.drawable.wrong_answer_border)
                }
            }

            // Update the button text for the next action
            if (mCurrentPosition == mQuestionsList!!.size) {
                binding.btnSubmit.text = "FINISH"
            } else {
                binding.btnSubmit.text = "NEXT QUESTION"
            }
        } else {
            // Move to the next question or finish the quiz
            mSelectedAnswers.clear()
            mCurrentPosition++
            if (mCurrentPosition <= mQuestionsList!!.size) {
                setQuestion()
                binding.btnSubmit.text = "SUBMIT"
            } else {
                // Quiz finished, navigate to the result screen
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                startActivity(intent)
                finish()
            }
        }
    }
}