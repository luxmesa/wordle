package wordle

import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

const val LENGTH = 5
const val PATH = "Resources/dictionary.txt"
// For this version, the best start word is "ROATE". You can override to
// speed up the first round.
const val OVERRIDE_START_WORD = ""//"ROATE"

fun main() {
    val allowedSet = generateWordList("Resources/wordle-allowed-guesses.txt")
    val answersSet = generateWordList("Resources/wordle-answers-alphabetical.txt")
    val usedSet = generateWordList("Resources/wordle-previous-answers.txt")
    val wordList = (allowedSet + answersSet).toList().sorted()
    val answersList = (answersSet - usedSet).toList().sorted()

    var wordCount = wordList.size
    val removed = BooleanArray(wordCount)

    var wordIndex = 0
    var answerIndex = 0

    // mark off words that aren't on the answer list
    while(answerIndex < answersList.size) {
        while(answersList[answerIndex] != wordList[wordIndex]) {
            removed[wordIndex] = true
            wordIndex++
            wordCount--
        }
        wordIndex++
        answerIndex++
    }

    var roundCount = 1
    var won = false
    var overrideStartIndex = wordList.indexOf(OVERRIDE_START_WORD)
    println("Fuckzilla Activated\n")
    while(wordCount >= 1 && !won) {
        println("Round $roundCount. Remaining words: ${wordCount}. Waiting for guess... ")
        val bestWord = if(overrideStartIndex != -1) overrideStartIndex else findBestWord(wordList, removed)
        overrideStartIndex = -1
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

fun generateWordList(filePath: String): Set<String> {
    val bufferedReader = BufferedReader(FileReader(filePath))

    val wordList = bufferedReader.lines()
            .filter { it.length == LENGTH }
            .map { it.toUpperCase() }
            .toList()
            .toSet()
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