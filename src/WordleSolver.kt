package wordle

import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

const val LENGTH = 5
const val PATH = "Resources/dictionary.txt"

fun main() {
    val wordList = generateWordList(PATH)

    var wordCount = wordList.size
    val removed = BooleanArray(wordCount)

    var roundCount = 1
    var won = false
    while(wordCount >= 1 && !won) {
        println("Round $roundCount. remaining words ${wordCount}. Waiting for guess... ")
        roundCount++
        val bestWord = findBestWord(wordList, removed)
        println("Best word: ${wordList[bestWord]}. ")
        if(removed[bestWord])
            println("This guess is no longer on the list")
        else
            println("This guess is on the list")
        print("Result(format 'BBYGB'): ")
        val result = readLine()!!.toUpperCase()
        println("")
        won = result == "GGGGG"
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
        println("Sweet!")
    else
        println("Sorry, I screwed up")
}

fun generateWordList(filePath: String): List<String> {
    val bufferedReader = BufferedReader(FileReader(filePath))

    val wordList = bufferedReader.lines()
            .filter { it.length == LENGTH }
            .toList()
    bufferedReader.close()
    return wordList
}

fun findBestWord(wordList: List<String>, removed: BooleanArray): Int {
    var bestWord = 0
    var bestScore = 0
    wordList.indices.forEach { guess ->
        val comparisons = HashMap<String, Int>()
        wordList.indices.filter { !removed[it] }.forEach { main ->
            val comparison = compareWords(wordList[main], wordList[guess])
            comparisons[comparison] = (comparisons[comparison] ?: 0) + 1
        }
        val score = comparisons.values.sumBy { it * it }
        if(bestScore == 0 || bestScore > score || (bestScore == score && !removed[guess])) {
            bestScore = score
            bestWord = guess
        }
    }
    return bestWord
}

fun compareWords(main: String, guess: String): String {
    var answer = ""
    for(i in 0 until LENGTH) {
        var c =
                if(main[i] == guess[i]) 'G'
                else {
                    var found = false
                    for(j in 0 until LENGTH) {
                        if(j != i && main[j] != guess[j] && main[j] == guess[i]) {
                            found = true
                            break
                        }
                    }
                    if(found) 'Y' else 'B'
                }
        answer += c
    }
    return answer
}