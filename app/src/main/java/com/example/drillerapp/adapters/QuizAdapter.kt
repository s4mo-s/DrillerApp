package com.example.drillerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drillerapp.R
import com.example.drillerapp.databinding.ItemQuizBinding
import org.json.JSONObject

class QuizAdapter(private val onQuizSelected: (String) -> Unit) :
    RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    private val quizzes = mutableListOf<String>()
    private var selectedPosition: Int = -1

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newQuizzes: List<String>) {
        quizzes.clear()
        quizzes.addAll(newQuizzes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizJson = quizzes[holder.adapterPosition]
        val jsonObject = JSONObject(quizJson)
        val quizName = jsonObject.optString("quizName", "Unknown Quiz")
        val quizDescription = jsonObject.optString("description", "No description available")

        holder.bind(quizName, quizDescription, holder.adapterPosition == selectedPosition)
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onQuizSelected(quizJson)
        }
    }

    override fun getItemCount(): Int = quizzes.size

    class QuizViewHolder(private val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String, description: String, isSelected: Boolean) {
            binding.tvQuizName.text = name
            binding.tvQuizDescription.text = description
            binding.root.setBackgroundResource(
                if (isSelected) R.drawable.selected_item_background else R.drawable.default_item_background
            )
        }
    }
}