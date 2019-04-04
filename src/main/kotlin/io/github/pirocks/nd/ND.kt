package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula

class NDProof(val statements: List<NDStatement>, val given: Set<FOLFormula>, val result: FOLFormula) {
    fun verify(given: Set<FOLFormula>): Boolean {
        return given.all { it in given } &&
                TODO()
    }
}

interface NDStatement {
    fun verify(given: Set<FOLFormula>): Boolean
    val proves: FOLFormula
}