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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drillerapp.adapters.QuizAdapter
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private var quizJsonString: String? = null
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper()
        quizAdapter = QuizAdapter { quizJson ->
            // Store the selected quiz JSON
            quizJsonString = quizJson
            Log.d("MainActivity", "Selected quiz JSON: $quizJsonString")
        }

        // Set up RecyclerView
        binding.rvQuizzes.layoutManager = LinearLayoutManager(this)
        binding.rvQuizzes.adapter = quizAdapter

        lifecycleScope.launch {
            val quizzes = withContext(Dispatchers.IO) {
                databaseHelper.getAllQuizzes()
            }
            quizAdapter.submitList(quizzes.map { it.toJson() })
        }


        // Start Quiz Button
        binding.btnStart.setOnClickListener {
            if (quizJsonString != null) {
                // Show BottomSheetDialog
                val bottomSheetDialog = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.dialog_randomize_quiz, binding.root, false)
                bottomSheetDialog.setContentView(view)

                val btnYes = view.findViewById<Button>(R.id.btn_yes)
                val btnNo = view.findViewById<Button>(R.id.btn_no)

                btnYes.setOnClickListener {
                    // Randomize quiz order
                    quizJsonString = randomizeQuizOrder(quizJsonString!!)
                    startQuiz()
                    bottomSheetDialog.dismiss()
                }

                btnNo.setOnClickListener {
                    // Start quiz without randomizing
                    startQuiz()
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
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

        // Upload Quizz Button
        binding.btnUploadQuiz.setOnClickListener {
            if (quizJsonString == null) {
                Toast.makeText(this, "Please load a quiz JSON file first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show a dialog to input quiz name and description
            val dialogView = layoutInflater.inflate(R.layout.dialog_upload_quiz, binding.root, false)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogView)

            val etQuizName = dialogView.findViewById<EditText>(R.id.et_quiz_name)
            val etQuizDescription = dialogView.findViewById<EditText>(R.id.et_quiz_description)
            val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit)

            btnSubmit.setOnClickListener {
                val quizName = etQuizName.text.toString().trim()
                val quizDescription = etQuizDescription.text.toString().trim()

                if (quizName.isEmpty() || quizDescription.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                try {
                    // Add name and description to the quiz JSON
                    val jsonObject = JSONObject(quizJsonString!!)
                    jsonObject.put("quizName", quizName)
                    jsonObject.put("description", quizDescription)

                    // Insert the quiz into MongoDB
                    lifecycleScope.launch(Dispatchers.IO) {
                        databaseHelper.insertQuiz(jsonObject.toString())

                        // Refresh the quiz list
                        val quizzes = databaseHelper.getAllQuizzes()
                        withContext(Dispatchers.Main) {
                            quizAdapter.submitList(quizzes.map { it.toJson() })
                            Toast.makeText(this@MainActivity, "Quiz uploaded successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error uploading quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.show()
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
    private fun randomizeQuizOrder(jsonString: String): String {
        return try {
            val jsonObject = JSONObject(jsonString)
            val questionsArray = jsonObject.getJSONArray("questions")

            // Convert JSONArray to a mutable list for shuffling
            val questionsList = mutableListOf<JSONObject>()
            for (i in 0 until questionsArray.length()) {
                questionsList.add(questionsArray.getJSONObject(i))
            }

            // Shuffle the questions
            questionsList.shuffle()

            // Replace the original questions array with the shuffled one
            val shuffledQuestionsArray = JSONArray()
            for (question in questionsList) {
                shuffledQuestionsArray.put(question)
            }
            jsonObject.put("questions", shuffledQuestionsArray)

            jsonObject.toString()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error randomizing quiz order: ${e.message}")
            jsonString // Return the original JSON if an error occurs
        }
    }
    private fun startQuiz() {
        val intent = Intent(this, QuestionsActivity::class.java)
        intent.putExtra("source", "json")
        intent.putExtra("quizData", quizJsonString ?: "")
        startActivity(intent)
        finish()
    }    
}