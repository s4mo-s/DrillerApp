package com.example.drillerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drillerapp.R
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizJson = quizzes[holder.adapterPosition] // Use holder.adapterPosition to get the current position
        val jsonObject = JSONObject(quizJson)
        val quizName = jsonObject.optString("quizName", "Untitled Quiz")
        val quizDescription = jsonObject.optString("description", "No description available")

        holder.bind(quizName, quizDescription, holder.adapterPosition == selectedPosition)
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition // Use holder.adapterPosition here
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onQuizSelected(quizJson)
        }
    }

    override fun getItemCount(): Int = quizzes.size

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvQuizName: TextView = itemView.findViewById(R.id.tv_quiz_name)
        private val tvQuizDescription: TextView = itemView.findViewById(R.id.tv_quiz_description)

        fun bind(name: String, description: String, isSelected: Boolean) {
            tvQuizName.text = name
            tvQuizDescription.text = description
            itemView.setBackgroundResource(
                if (isSelected) R.drawable.selected_item_background else R.drawable.default_item_background
            )
        }
    }
}