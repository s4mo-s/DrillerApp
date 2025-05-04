package com.example.drillerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drillerapp.adapters.HistoryAdapter
import com.example.drillerapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        historyAdapter = HistoryAdapter { quizResult ->
            if (quizResult.score < quizResult.totalQuestions) {
                // Retake quiz
                val intent = Intent(this, QuestionsActivity::class.java)
                intent.putExtra("quizName", quizResult.quizName)
                intent.putExtra("quizData", quizResult.quizJson)
                startActivity(intent)
            }
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter

        loadQuizHistory()
    }

    private fun loadQuizHistory() {
        lifecycleScope.launch {
            val databaseHelper = DatabaseHelper()
            val history = withContext(Dispatchers.IO) {
                databaseHelper.getAllResults()
            }

            if (history.isEmpty()) {
                Log.d("HistoryActivity", "No quiz history found")
            } else {
                Log.d("HistoryActivity", "Loaded quiz history: $history")
            }

            historyAdapter.submitList(history)
        }
    }
}