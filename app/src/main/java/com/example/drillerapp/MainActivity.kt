package com.example.drillerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drillerapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private var quizJsonString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start Quiz Button
        binding.btnStart.setOnClickListener {
            if (quizJsonString != null) {
                val intent = Intent(this, QuestionsActivity::class.java)
                intent.putExtra("source", "json")
                intent.putExtra("quizData", quizJsonString)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please load a quiz JSON file first!", Toast.LENGTH_SHORT).show()
            }
        }

        // Load JSON Button
        binding.btnLoadJson.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/json"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            filePickerLauncher.launch(intent)
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val jsonString = reader.readText()
                    reader.close()

                    // Validate JSON
                    if (isValidQuizJson(jsonString)) {
                        quizJsonString = jsonString
                        val jsonObject = JSONObject(quizJsonString ?: throw Exception())
                        val questions = jsonObject.getJSONArray("questions")
                        Toast.makeText(this, "Loaded ${questions.length()} questions!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Invalid quiz JSON format!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidQuizJson(jsonString: String): Boolean {
        return try {
            val jsonObject = JSONObject(jsonString)
            val questionsArray = jsonObject.getJSONArray("questions")

            for (i in 0 until questionsArray.length()) {
                val questionObject = questionsArray.getJSONObject(i)

                // Check if required fields are present
                if (!questionObject.has("question") ||
                    !questionObject.has("answers") ||
                    !questionObject.has("correctAnswers")
                ) {
                    return false
                }

                // Validate answers array
                val answersArray = questionObject.getJSONArray("answers")
                if (answersArray.length() < 2 || answersArray.length() > 6) { // Ensure 2-6 answers
                    return false
                }

                // Validate correctAnswers array
                val correctAnswersArray = questionObject.getJSONArray("correctAnswers")
                if (correctAnswersArray.length() == 0) { // Ensure at least one correct answer
                    return false
                }

                // Ensure all correct answers are in the answers array
                for (j in 0 until correctAnswersArray.length()) {
                    if (!answersArray.toString().contains(correctAnswersArray.getString(j))) {
                        return false
                    }
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}