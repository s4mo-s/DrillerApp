package com.example.drillerapp

import android.annotation.SuppressLint
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import android.util.Log

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
    private val collection: MongoCollection<Document> = database.getCollection("quizzes")

    fun insertQuiz(jsonString: String) {
        val document = Document.parse(jsonString)
        collection.insertOne(document)
        Log.d("DatabaseHelper", "Inserted quiz: $jsonString")
    }

    fun getAllQuizzes(): List<Document> {
        try {
            Log.d("DatabaseHelper", "Getting all quizzes")
            val quizzes = collection.find().toList()
            Log.d("DatabaseHelper", "Quizzes: $quizzes")
            return quizzes
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching quizzes: ${e.message}")
            return emptyList()
        }

    }
}
