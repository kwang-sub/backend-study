package ch06.item37

import java.util.EnumMap

enum class Phase {
    SOLID, LIQUID, GAS;

    enum class Transition(
        val from: Phase,
        val to: Phase,
    ) {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID), BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        companion object {
            private val m = EnumMap(entries.groupBy { it.from }
                .let { target ->
                    val enumMap = EnumMap<Phase, EnumMap<Phase, Transition>>(Phase::class.java)
                    target.forEach { target2 ->
                        enumMap[target2.key] = target2.value
                            .let { transitionList ->
                                val result: EnumMap<Phase, Transition> = EnumMap(Phase::class.java)
                                transitionList.forEach { result[it.to] = it }
                                result
                            }
                    }
                    enumMap
                }
            )

            fun from(from: Phase, to: Phase): Transition {
                println(m)
                return m[from]?.get(to) ?: throw IndexOutOfBoundsException()
            }
        }
    }
}

fun main() {
    println(Phase.Transition.from(Phase.SOLID, Phase.LIQUID))
}