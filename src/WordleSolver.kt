package wordle

import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

const val LENGTH = 5
const val PATH = "Resources/dictionary.txt"

fun main() {
    //val allowedList = generateWordList("Resources/wordle-allowed-guesses.txt")
    //val answersList = generateWordList("Resources/wordle-answers-alphabetical.txt");
    //val wordList = (allowedList + answersList).sorted()
    val wordList = generateWordList(PATH)

    var wordCount = wordList.size
    val removed = BooleanArray(wordCount)

    /*var wordIndex = 0
    var answerIndex = 0
    while(answerIndex < answersList.size) {
        while(answersList[answerIndex] != wordList[wordIndex]) {
            removed[wordIndex] = true
            wordIndex++
            wordCount--
        }
        wordIndex++
        answerIndex++
    }*/

    var roundCount = 1
    var won = false
    println("Fuckzilla Activated\n")
    while(wordCount >= 1 && !won) {
        println("Round $roundCount. Remaining words: ${wordCount}. Waiting for guess... ")
        val bestWord = findBestWord(wordList, removed)
        roundCount++
        println("Best word: ${wordList[bestWord]}. ")
        if(removed[bestWord])
            println("This guess is no longer on the list")
        else
            println("This guess is on the list")
        print("Result(format 'BBYGB'): ")
        val result = inputToInt(readLine()!!)
        println("")
        won = result == 0
        if(!won) {
            wordList.indices.filter { !removed[it] }.forEach {
                val comparison = compareWords(wordList[it], wordList[bestWord])
                if (comparison != result) {
                    removed[it] = true
                    wordCount--
                }
            }
        }
    }
    if(won)
        println("Fuckzilla is victorious!")
    else
        println("Fuckzilla has been defeated!")
}

fun inputToInt(input: String): Int {
    var answer = 0
    input.toUpperCase().chars().forEach {
        answer *= 3
        if(it == 'Y'.toInt())
            answer += 1
        if(it == 'B'.toInt())
            answer += 2
    }
    return answer
}

fun generateWordList(filePath: String): List<String> {
    val bufferedReader = BufferedReader(FileReader(filePath))

    val wordList = bufferedReader.lines()
            .filter { it.length == LENGTH }
            .map { it.toUpperCase() }
            .toList()
    bufferedReader.close()
    return wordList
}

fun findBestWord(wordList: List<String>, removed: BooleanArray): Int {
    var bestWord = 0
    var bestScore = Integer.MAX_VALUE
    wordList.indices.forEach { guess ->
        val comparisons = HashMap<Int, Int>()
        wordList.indices.filter { !removed[it] }.forEach { main ->
            val comparison = compareWords(wordList[main], wordList[guess])
            comparisons[comparison] = (comparisons[comparison] ?: 0) + 1
        }
        val score = comparisons.filter { it.key != 0 }.values.sumBy { it * it }
        if(bestScore > score) {
            bestScore = score
            bestWord = guess
        }
    }
    return bestWord
}

fun compareWords(main: String, guess: String): Int {
    var answer = 0
    var letters = IntArray(26)
    for(i in 0 until LENGTH) {
        if(main[i] != guess[i])
            letters[main[i] - 'A']++
    }
    for(i in 0 until LENGTH) {
        answer *= 3
        var c =
                if(main[i] == guess[i]) 0
                else {
                    if(letters[guess[i] - 'A'] > 0) {
                        letters[guess[i] - 'A']--
                        1
                    } else 2
                }
        answer += c
    }
    return answer
}