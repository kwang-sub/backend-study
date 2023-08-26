package ch05.item30

fun <T: Number> union(s1: Set<T>, s2: Set<T>): Set<T> {
    return s1.union(s2)
}

fun main() {
    val s1: Set<Long> = setOf<Long>(123L, 12355L)
    val s2: Set<Int> = setOf<Int>(1223, 123, 1)
    val union = union(s1, s2)
    println(union::class.java)
}


