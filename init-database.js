db = db.getSiblingDB("drillerapp"); // Use or create the "drillerapp" database

db.quizzes.insertMany([
    {
        quizName: "General Knowledge Quiz",
        description: "A quiz to test your general knowledge.",
        questions: [
            {
                question: "What is the capital of France?",
                answers: ["Paris", "London", "Berlin", "Madrid"],
                correctAnswers: ["Paris"]
            },
            {
                question: "What is 2 + 2?",
                answers: ["3", "4", "5", "6"],
                correctAnswers: ["4"]
            }
        ]
    },
    {
        quizName: "Science Quiz",
        description: "A quiz to test your science knowledge.",
        questions: [
            {
                question: "What planet is known as the Red Planet?",
                answers: ["Earth", "Mars", "Jupiter", "Venus"],
                correctAnswers: ["Mars"]
            },
            {
                question: "What is the chemical symbol for water?",
                answers: ["H2O", "O2", "CO2", "NaCl"],
                correctAnswers: ["H2O"]
            }
        ]
    }
]);

db.results.insertOne(
    {
        quizName: "Science Quiz",
        score: 1,
        totalQuestions: 2,
        quizJson: JSON.stringify({
            questions: [
                 {
                     question: "What planet is known as the Red Planet?",
                     answers: ["Earth", "Mars", "Jupiter", "Venus"],
                     correctAnswers: ["Mars"]
                 },
                 {
                     question: "What is the chemical symbol for water?",
                     answers: ["H2O", "O2", "CO2", "NaCl"],
                     correctAnswers: ["H2O"]
                 }
            ]
        })
    }
);