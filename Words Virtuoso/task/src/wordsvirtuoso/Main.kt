package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

class Words {
    private val wordsSet = mutableSetOf<String>()
    fun getWordsSet() = wordsSet
    fun addWord(word: String) = wordsSet.add(word.lowercase())
    fun getRandomWord(): String {
        return wordsSet.toList()[Random.nextInt(0, wordsSet.size)]
    }
}

class User {
    var attemptsCount = 0
    val wrongCharacters = mutableSetOf<Char>()
    val attemptsWords = mutableListOf<String>()
    private var startTime: Long = 0
    private var endTime: Long = 0
    var correctAnswer = false

    fun startTimer() {
        startTime = System.currentTimeMillis()
    }

    fun stopTimer() {
        endTime = System.currentTimeMillis()
    }

    fun duration(): Long = (endTime - startTime) / 1000

    fun displayAttempts() {
        println()
        for (w in attemptsWords) {
            println(w)
        }
        val wrongCharString = wrongCharacters.toList().sorted().joinToString("")
        if (wrongCharString.isNotEmpty() && !correctAnswer) println("\n\u001B[48:5:14m$wrongCharString\u001B[0m")
    }
}

/**
 * Checks input word for the right length and characters
 */
fun wordAnalysis(word: String): Boolean {
    return !(word.length != 5 ||
            !"""[a-zA-Z]{5}""".toRegex().matches(word) ||
            """.*(.).*\1+.*""".toRegex().matches(word.lowercase()))
}

/**
 * Checks user input
 */
fun inputWordAnalysis(word: String, allWords: Words): Boolean {
    return if (word.length != 5) {
        println("The input isn't a 5-letter word.")
        false
    } else if (!"""[a-zA-Z]{5}""".toRegex().matches(word)) {
        println("One or more letters of the input aren't valid.")
        false
    } else if (""".*(.).*\1+.*""".toRegex().matches(word.lowercase())) {
        println("The input has duplicate letters.")
        false
    } else if (!allWords.getWordsSet().contains(word)) {
        println("The input word isn't included in my words list.")
        false
    } else {
        true
    }
}

/**
 * Checks and adds correct words to object's set
 */
fun addWordsFromList(list: List<String>, wordsCollection: Words, fileName: String) {
    var invalidWordsCount = 0
    for (w in list) {
        if (wordAnalysis(w)) {
            wordsCollection.addWord(w)
        } else {
            invalidWordsCount++
        }
    }
    if (invalidWordsCount != 0) {
        println("Error: $invalidWordsCount invalid words were found in the $fileName file.")
        exitProcess(0)
    }
}

/**
 * Reads a file with words and checks if the words are valid
 */
fun wordsVirtuoso(args: Array<String>) {
    val allWordsCollection = Words()
    val candidateWordsCollection = Words()
    val allWordsFile: List<String>
    val candidateWordsFile: List<String>
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        exitProcess(0)
    } else {
        try { allWordsFile = File(args[0]).readLines() } catch (e: Exception) {
            println("Error: The words file ${args[0]} doesn't exist.")
            exitProcess(0)
        }
        try { candidateWordsFile = File(args[1]).readLines() } catch (e: Exception) {
            println("Error: The candidate words file ${args[1]} doesn't exist.")
            exitProcess(0)
        }
        addWordsFromList(allWordsFile, allWordsCollection, args[0])
        addWordsFromList(candidateWordsFile, candidateWordsCollection, args[1])
        val includedWords = candidateWordsCollection.getWordsSet() - allWordsCollection.getWordsSet()
        if (includedWords.isNotEmpty()) {
            println("Error: ${includedWords.size} candidate words are not included in the ${args[0]} file.")
            exitProcess(0)
        }
        startGame(allWordsCollection, candidateWordsCollection)
    }
}

/**
 * Builds clue word based on user word's letters
 */
fun clueWord(randomWord: String, userWord: String, userObject: User) {
    var clueWord = ""
    for (i in userWord.indices) {
        clueWord += if (userWord[i] == randomWord[i]) {
            "\u001B[48:5:10m${userWord[i].uppercaseChar()}\u001B[0m"
        } else if (randomWord.contains(userWord[i])) {
            "\u001B[48:5:11m${userWord[i].uppercaseChar()}\u001B[0m"
        } else {
            userObject.wrongCharacters.add(userWord[i].uppercaseChar())
            "\u001B[48:5:7m${userWord[i].uppercaseChar()}\u001B[0m"
        }
    }
    userObject.attemptsWords.add(clueWord)
    if (userWord.lowercase() == randomWord.lowercase()) userObject.correctAnswer = true
    userObject.displayAttempts()
}

/**
 * Guess word game with hints
 */
fun startGame(allWords: Words, candidateWords: Words) {
    println("Words Virtuoso")
    val randomWord = candidateWords.getRandomWord().lowercase()
    val userObject = User()
    do {
        println("\nInput a 5-letter word:")
        userObject.startTimer()
        val userInput = readln().lowercase()
        if (userInput == "exit") {
            println("\nThe game is over.")
            exitProcess(0)
        }
        userObject.attemptsCount++
        if (inputWordAnalysis(userInput, allWords)) {
            clueWord(randomWord, userInput, userObject)
            if (userInput == randomWord) {
                userObject.stopTimer()
                println("\nCorrect!")
                if (userObject.attemptsCount == 1) {
                    println("Amazing luck! The solution was found at once.")
                } else {
                    println("The solution was found after ${userObject.attemptsCount} " +
                            "tries in ${userObject.duration()} seconds.")
                }
                exitProcess(0)
            }
        }
    } while(true)
}

fun main(args: Array<String>) {
    wordsVirtuoso(args)
}
