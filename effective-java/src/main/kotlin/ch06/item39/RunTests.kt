package ch06.item39

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun main() {
    var tests = 0
    var passed = 0
    val testClass = Sample.Companion::class.java

    for (m: Method in testClass.getDeclaredMethods()) {
        if (m.isAnnotationPresent(Test::class.java)) {
            tests += 1
            try {
                m.invoke(null)
                passed++
            } catch (wrappedExe: InvocationTargetException) {
                val exc = wrappedExe.cause
                println("$m 실패 $exc")
            } catch (exc: Exception) {
                println("잘못 사용한 @Test: $m")
            }
        }
    }
    println("성공 $passed 실패 ${tests - passed}")
}