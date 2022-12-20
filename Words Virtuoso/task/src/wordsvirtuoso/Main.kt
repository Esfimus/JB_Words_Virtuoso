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
fun clueWord(randomWord: String, userWord: String) {
    var clueList = ""
    for (i in userWord.indices) {
        clueList += if (userWord[i] == randomWord[i]) {
            userWord[i].uppercaseChar()
        } else if (randomWord.contains(userWord[i])) {
            userWord[i]
        } else {
            '_'
        }
    }
    println(clueList)
}

/**
 * Guess word game with hints
 */
fun startGame(allWords: Words, candidateWords: Words) {
    println("Words Virtuoso")
    val randomWord = candidateWords.getRandomWord()
    do {
        println("\nInput a 5-letter word:")
        val userInput = readln().lowercase()
        if (userInput == "exit") {
            println("\nThe game is over.")
            exitProcess(0)
        }
        if (inputWordAnalysis(userInput, allWords)) {
            if (userInput == randomWord) {
                println("\nCorrect!")
                exitProcess(0)
            } else {
                clueWord(randomWord, userInput)
            }
        }
    } while(true)
}

fun main(args: Array<String>) {
    wordsVirtuoso(args)
}
