package wordsvirtuoso

import java.io.File

class Words {
    private val wordsSet = mutableSetOf<String>()
    fun getWordsSet() = wordsSet
    fun addWord(word: String) = wordsSet.add(word)
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
 * Reads a file with words and checks if the words are valid
 */
fun wordsVirtuoso() {
    lateinit var fileName: String
    try {
        // reading a file
        println("Input the words file:")
        fileName = readln()
        val fileList = File(fileName).readLines()
        val wordsCollection = Words()
        var invalidWordsCount = 0
        // analyzing every word
        for (w in fileList) {
            if (wordAnalysis(w)) {
                wordsCollection.addWord(w)
            } else {
                invalidWordsCount++
            }
        }
        if (invalidWordsCount == 0) {
            println("All words are valid!")
        } else {
            println("Warning: $invalidWordsCount invalid words were found in the $fileName file.")
        }
    } catch (e: Exception) {
        println("Error: The words file $fileName doesn't exist.")
    }
}

fun main() {
    wordsVirtuoso()
}
