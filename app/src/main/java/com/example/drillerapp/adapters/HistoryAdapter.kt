package com.example.drillerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drillerapp.R
import com.example.drillerapp.models.QuizResult
import com.example.drillerapp.databinding.ItemHistoryBinding

class HistoryAdapter(private val onRetakeClick: (QuizResult) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val history = mutableListOf<QuizResult>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newHistory: List<QuizResult>) {
        history.clear()
        history.addAll(newHistory.reversed())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val quizResult = history[position]
        holder.bind(quizResult)
        holder.binding.btnRetake.setOnClickListener {
            onRetakeClick(quizResult)
        }
    }

    override fun getItemCount(): Int = history.size

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(quizResult: QuizResult) {
            binding.tvQuizNameResult.text = quizResult.quizName
            binding.tvScore.text = "Score: ${quizResult.score}/${quizResult.totalQuestions}"
            
            // Show "Success" if full score, otherwise show "Retake" button
            if (quizResult.score == quizResult.totalQuestions) {
                binding.tvSuccess.visibility = View.VISIBLE
                binding.btnRetake.visibility = View.GONE
            } else {
                binding.tvSuccess.visibility = View.GONE
                binding.btnRetake.visibility = View.VISIBLE
            }
        }
    }
}