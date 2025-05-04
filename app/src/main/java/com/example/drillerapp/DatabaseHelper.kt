package com.example.drillerapp

import android.annotation.SuppressLint
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import android.util.Log
import com.example.drillerapp.models.QuizResult

object MongoDBConnection {
    @SuppressLint("AuthLeak")
    private const val CONNECTION_STRING = "mongodb://10.0.2.2:27017"

    fun getMongoClient(): MongoClient {
        return MongoClients.create(CONNECTION_STRING)
    }
}

class DatabaseHelper {
    private val mongoClient = MongoDBConnection.getMongoClient()
    private val database: MongoDatabase = mongoClient.getDatabase("drillerapp")
    private val quizCollection: MongoCollection<Document> = database.getCollection("quizzes")
    private val resultCollection: MongoCollection<Document> = database.getCollection("results")

    fun insertQuiz(jsonString: String) {
        val document = Document.parse(jsonString)
        quizCollection.insertOne(document)
        Log.d("DatabaseHelper", "Inserted quiz: $jsonString")
    }

    fun getAllQuizzes(): List<Document> {
        try {
            Log.d("DatabaseHelper", "Getting all quizzes")
            val quizzes = quizCollection.find().toList()
            Log.d("DatabaseHelper", "Quizzes: $quizzes")
            return quizzes
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching quizzes: ${e.message}")
            return emptyList()
        }
    }

    fun insertResult(quizResult: QuizResult) {
        try {
            val document = Document()
            document["quizName"] = quizResult.quizName
            document["score"] = quizResult.score
            document["totalQuestions"] = quizResult.totalQuestions
            document["quizJson"] = quizResult.quizJson
            resultCollection.insertOne(document)
            Log.d("DatabaseHelper", "Inserted quiz result: $quizResult")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting quiz result: ${e.message}")
        }
    }

    fun getAllResults(): List<QuizResult> {
        return try {
            Log.d("DatabaseHelper", "Getting all quiz results")
            val results = resultCollection.find().map { document ->
                QuizResult(
                    quizName = document.getString("quizName") ?: "Unknown quiz",
                    score = document.getInteger("score"),
                    totalQuestions = document.getInteger("totalQuestions"),
                    quizJson = document.get("quizJson").toString()
                )
            }.toList()
            Log.d("DatabaseHelper", "Quiz results: $results")
            results
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching quiz results: ${e.message}")
            emptyList()
        }
    }
}
