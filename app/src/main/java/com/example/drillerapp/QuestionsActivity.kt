package com.example.drillerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.example.drillerapp.databinding.ActivityQuestionsBinding

class QuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityQuestionsBinding
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<QuestionModel>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mCorrectAnswers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mQuestionsList = Constants.getQuestions()

        setQuestion()

        if (binding.btnSubmit.text == "SUBMIT") {
            binding.tvOptionOne.setOnClickListener(this)
            binding.tvOptionTwo.setOnClickListener(this)
            binding.tvOptionThree.setOnClickListener(this)
            binding.tvOptionFour.setOnClickListener(this)
        }
        binding.btnSubmit.setOnClickListener(this)
    }

    private fun setQuestion(){
        val question = mQuestionsList!![mCurrentPosition - 1]

        defaultOptionsView()

        binding.btnSubmit.text = "SUBMIT"
        binding.progressBar.progress = mCurrentPosition
        binding.progressBar.max = mQuestionsList!!.size
        binding.tvProgress.text = "${mCurrentPosition} / ${binding.progressBar.max}"

        binding.tvQuestion.text = question.question
        binding.tvOptionOne.text = question.option1
        binding.tvOptionTwo.text = question.option2
        binding.tvOptionThree.text = question.option3
        binding.tvOptionFour.text = question.option4
    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        options.add(0, binding.tvOptionOne)
        options.add(1, binding.tvOptionTwo)
        options.add(2, binding.tvOptionThree)
        options.add(3, binding.tvOptionFour)

        for (option in options) {
            option.setTextColor("#7A8000".toColorInt())
            option.typeface = android.graphics.Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this, R.drawable.default_option_border)
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int) {
        if (binding.btnSubmit.text != "SUBMIT") {
            return
        }
        defaultOptionsView()
        mSelectedOptionPosition = selectedOptionNum

        tv.setTextColor("#363A00".toColorInt())
        tv.setTypeface(tv.typeface, android.graphics.Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this, R.drawable.selected_option_border)
    }

    private fun answerView(answer: Int, drawableView: Int) {
        when(answer) {
            1 -> binding.tvOptionOne.background = ContextCompat.getDrawable(this, drawableView)
            2 -> binding.tvOptionTwo.background = ContextCompat.getDrawable(this, drawableView)
            3 -> binding.tvOptionThree.background = ContextCompat.getDrawable(this, drawableView)
            4 -> binding.tvOptionFour.background = ContextCompat.getDrawable(this, drawableView)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_option_one -> selectedOptionView(binding.tvOptionOne, 1)
            R.id.tv_option_two -> selectedOptionView(binding.tvOptionTwo, 2)
            R.id.tv_option_three -> selectedOptionView(binding.tvOptionThree, 3)
            R.id.tv_option_four -> selectedOptionView(binding.tvOptionFour, 4)
            R.id.btn_submit -> submit()
        }
    }

    private fun submit() {
        if(mSelectedOptionPosition == 0){
            mCurrentPosition++

            when {
                mCurrentPosition <= mQuestionsList!!.size -> setQuestion()
                else -> {
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                    intent.putExtra(Constants.TOTAL_QUESTIONS, mQuestionsList!!.size)
                    startActivity(intent)
                    finish()
                }
            }
        }
        else {
            val question = mQuestionsList?.get(mCurrentPosition - 1)
            if(question!!.correctAnswer != mSelectedOptionPosition) {
                answerView(mSelectedOptionPosition, R.drawable.wrong_option_border)
            }
            else {
                mCorrectAnswers++
            }
            answerView(question.correctAnswer, R.drawable.correct_option_border)

            if(mCurrentPosition == mQuestionsList!!.size) {
                binding.btnSubmit.text = "FINISH"
            }
            else {
                binding.btnSubmit.text = "GO TO NEXT QUESTION"
            }
            mSelectedOptionPosition = 0
        }
    }
}