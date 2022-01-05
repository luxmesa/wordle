package wordle

import java.io.BufferedReader
import java.io.FileReader
import kotlin.streams.toList

const val LENGTH = 5
const val PATH = "Resources/dictionary.txt"

fun main() {
    val wordList = generateWordList(PATH)
    println(wordList.size)

    val removed = BooleanArray(wordList.size)
    val comparisons = Array(wordList.size) { main ->
        Array(wordList.size) { guess ->
            compareWords(wordList[main], wordList[guess])
        }
    }


}

fun generateWordList(filePath: String): List<String> {
    val bufferedReader = BufferedReader(FileReader(filePath))

    val wordList = bufferedReader.lines()
            .filter { it.length == LENGTH }
            .toList()
    bufferedReader.close()
    return wordList
}

fun compareWords(main: String, guess: String): String {
    val letters = BooleanArray(26)
    main.chars().forEach {
        letters[it - 'A'.toInt()] = true
    }
    var answer = ""
    for(i in 0 until LENGTH) {
        var c =
                if(main[i] == guess[i]) 'G'
                else if(letters[guess[i].toInt() - 'A'.toInt()]) 'Y'
                else 'B'
        answer += c
    }
    return answer
}