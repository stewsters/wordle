package com.stewsters.wordle

import java.io.File

// TODO: add command based updating
// TODO: need a list of letters we know do not occur in a position
// TODO: need an actual wordle list
fun main() {

    val allWords = File("words.txt").readLines().filter { it.length == 5 && it.all { it.isLetter() } }.map { it.lowercase() }

//    val tried = listOf(
//        "audio"
//    )

    // When we get results that

    val known = listOf<Char?>(null, 'e', null, null, null)
    val contains = listOf<Char>('e', 'r', 'p', 'y')
    val invalid = listOf<Char>('a', 'u', 'd', 'i', 'o', 'b', 'n', 'c', 'h', 's', 't', 'l')

//    val triedCharacters = tried.flatMap { it.toCharArray().toList() }

    val validWords = allWords.filter { potentialWord ->
        if (invalid.any { potentialWord.contains(it) })
            return@filter false

        if (!contains.all { potentialWord.contains(it) })
            return@filter false

//
        known.forEachIndexed { index, c ->
            if (c != null && potentialWord[index] != c)
                return@filter false
        }

        true
    }

    println(validWords)

    // find most common letters we have not tried yet
    val charsToExplore = validWords.flatMap { it.toCharArray().toList() }
        .filter { !invalid.contains(it) && !contains.contains(it) }
        .groupingBy { it }.eachCount()

    println("Characters we are looking for")
    println(charsToExplore.toList().sortedByDescending { it.second })

    val mostInfoGain = validWords.maxByOrNull { potentialWord ->
        potentialWord.toCharArray().toList().distinct().sumOf { charsToExplore[it] ?: 0 }
    }

    println("Most info gain " + mostInfoGain)


//    ('a'..'z').toList()

}

// maximize information gain?

//
//class Solving(){
//    val
//
//}
//
//class CharSpot(
//    val index:Int
//){
//    val options = ('a'..'z').toMutableList()
//    val invalid = mutableListOf<Char>()
//}