package ch06.item37

import java.util.*

class Plant(
    val name: String,
    val lifeCycle: LifeCycle,
) {
    enum class LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

    override fun toString(): String {
        return name
    }
}

fun main() {
    val plantsByLifeCycle: MutableMap<Plant.LifeCycle, MutableSet<Plant>> = EnumMap(Plant.LifeCycle::class.java)
    for (lc: Plant.LifeCycle in Plant.LifeCycle.entries) {
        plantsByLifeCycle[lc] = mutableSetOf()
    }
    val garden = listOf(Plant("1", Plant.LifeCycle.PERENNIAL), Plant("2", Plant.LifeCycle.ANNUAL))
    for (plant in garden) {
        plantsByLifeCycle[plant.lifeCycle]?.add(plant)
    }

    println(plantsByLifeCycle)
}