package wordsvirtuoso

import java.io.File
import kotlin.system.exitProcess

class Words {
    private val wordsSet = mutableSetOf<String>()
    fun getWordsSet() = wordsSet
    fun addWord(word: String) = wordsSet.add(word.lowercase())
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
        println("Words Virtuoso")
    }
}

fun main(args: Array<String>) {
    wordsVirtuoso(args)
}
