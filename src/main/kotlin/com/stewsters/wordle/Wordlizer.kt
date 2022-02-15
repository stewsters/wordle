package com.stewsters.wordle

import java.io.File
import kotlin.math.max

// TODO: add command based updating
// TODO: need a list of letters we know do not occur in a position
// TODO: need an actual wordle list
val allWords =
    (
            File("answers.txt").readLines() //+
//                    File("allowed-guesses.txt").readLines() +
//                    File("cleanwords.txt").readLines()
            ) //
        .filter { it.length == 5 && it.all { it.isLetter() } }
        .map { it.lowercase() }
        .distinct()
        .toMutableList()

fun main() {
//    println(allWords.filter { it[1]=='l' && it[3]=='e' && it[4]=='r' && listOf('t','u','i','p','a','s','b').all {char-> !it.contains(char) }  })
//    File("cleanwords.txt").writeText(allWords.joinToString("\n"))

    val solve = Solving()

    // TODO: something is wrong with the last one, humor should be getting matched
//    solve.process("slate", "bbbbb")
//    solve.process("crony", "byybb")
//    solve.process("furor", "bgbgg") // This last one fails since there are 2 rs, one in the right and one in the wrong

    while (!solve.isSolved()) {
        val validWords = solve.filterValid(allWords)

        // find most common letters we have not tried yet
        val charsToExplore = mutableMapOf<Int, Map<Char, Int>>()

        (0 until 5).forEach { charPos ->
            charsToExplore[charPos] = validWords
                .map { it.toCharArray().toList()[charPos] }
                .filter { !solve.invalid.contains(it) && !solve.contains.contains(it) }
                .groupingBy { it }
                .eachCount()
        }


        println("Characters we are looking for")
        (0 until 5).forEach { charPos ->
            println("$charPos " + charsToExplore[charPos]?.toList()?.sortedByDescending { it.second })
        }

        val highValueWords = validWords.sortedByDescending { potentialWord ->
            potentialWord.toCharArray().toList()
                .mapIndexed { index, c -> Pair(c, charsToExplore[index]?.get(c) ?: 0) }
                .groupingBy { it.first }
                .fold(0) { o, n -> max(o, n.second) }
                .entries.sumOf { it.value }
        }
        println(highValueWords.take(10))

        // maximize information gain?
        val mostInfoGain = highValueWords.take(1)

        println("Most info gain " + mostInfoGain)

        if (mostInfoGain.isEmpty()) {
            println("No valid words left")
            return
        }

        println("How did that go? g b y   r")
        val input = readln()
        solve.process(mostInfoGain.first(), input)

    }
}

class Solving() {
    val known = (0 until 5).map { CharSpot() }
    val contains = mutableListOf<Char>()
    val invalid = mutableListOf<Char>()

    fun filterValid(allWords: List<String>): List<String> {
        return allWords.filter { potentialWord ->
            if (invalid.any { potentialWord.contains(it) })
                return@filter false

            if (!contains.all { potentialWord.contains(it) })
                return@filter false

            known.forEachIndexed { index, c ->
                if (!c.options.contains(potentialWord[index]))
                    return@filter false
            }

            true
        }
    }

    fun isSolved(): Boolean {
        return known.all { it.options.size == 1 }
    }

    fun process(tried: String, correct: String) {
        // green
        // black
        // yellow
        // r remove
        if (correct == "r") {
            println("Removing this one")
            allWords.remove(tried)
            return
        }
        if (tried.length != correct.length) {
            println("Wrong length")
            return
        }
        if (!listOf('g', 'b', 'y').containsAll(correct.trim().toCharArray().toList().distinct())) {
            println("invalid characters")
            return
        }


        (0 until 5).forEach { i ->
            val cs = known[i]
            val triedChar = tried[i]
            when (correct[i]) {
                'g' -> cs.options.removeAll { it != triedChar }
                'y' -> {
                    cs.options.remove(triedChar) // it's not here
                    contains.add(triedChar)
                }
                'b' -> {
                    cs.options.remove(triedChar)
                    invalid.add(triedChar)
                }

            }
        }

    }

}

class CharSpot {
    val options = ('a'..'z').toMutableList()
}