package ch07.item42

import java.util.*


fun main() {
    val words = listOf("asdf", "zzzz")
    Collections.sort(words, Comparator() { s1, s2 ->
        return@Comparator s1.length.compareTo(s2.length)
    })
}
