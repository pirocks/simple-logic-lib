package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import io.github.pirocks.logic.True

fun proof(givens: Set<FOLFormula> = emptySet(), result: FOLFormula, init: MutableList<NDStatement>.() -> Unit): NDProof {
    val statements = mutableListOf<NDStatement>()
    statements.init()
    return NDProof(statements, givens, result)
}

fun MutableList<NDStatement>.implies(assumptionStatement: AssumptionStatement, init: MutableList<NDStatement>.(assumption: NDStatement) -> Unit): ImpliesIntroduction {
    val statements = mutableListOf<NDStatement>()
    statements.init(assumptionStatement)
    val implication = ImpliesIntroduction(assumptionStatement, statements)
    add(implication)
    return implication
}

fun MutableList<NDStatement>.trueIntro(): TruthIntroduction {
    val element = TruthIntroduction()
    add(element)
    return element
}

fun MutableList<NDStatement>.andIntro(left: NDStatement, right: NDStatement): AndIntroduction {
    val element = AndIntroduction(left, right)
    add(element)
    return element
}

//fun MutableList<NDStatement>.forAllIntro(){
//
//}
//fun MutableList<NDStatement>.existsIntro(){
//
//}
fun MutableList<NDStatement>.orIntroLeft(left: FOLFormula, right: NDStatement): OrIntroductionLeft {
    val element = OrIntroductionLeft(left, right)
    add(element)
    return element
}

fun MutableList<NDStatement>.orIntroRight(left: NDStatement, right: FOLFormula): OrIntroductionRight {
    val element = OrIntroductionRight(left, right)
    add(element)
    return element
}

fun MutableList<NDStatement>.iFFIntro(leftImplication: NDStatement, rightImplication: NDStatement): IFFIntroduction {
    val element = IFFIntroduction(leftImplication, rightImplication)
    add(element)
    return element
}

fun MutableList<NDStatement>.negationIntro(assumptionStatement: AssumptionStatement, init: MutableList<NDStatement>.(assumption: NDStatement) -> Unit): ImpliesIntroduction {
    val statements = mutableListOf<NDStatement>()
    statements.init(assumptionStatement)
    val implication = ImpliesIntroduction(assumptionStatement, statements)
    add(implication)
    return implication
}

fun MutableList<NDStatement>.falseIntro(contradictoryLeft: NDStatement, contradictoryRight: NDStatement): FalseIntroduction {
    val element = FalseIntroduction(contradictoryLeft, contradictoryRight)
    add(element)
    return element
}

fun MutableList<NDStatement>.idIntro(toCopy: NDStatement): IDIntroduction {
    val element = IDIntroduction(toCopy)
    add(element)
    return element;
}

fun assume(formula: FOLFormula): AssumptionStatement {
    return AssumptionStatement(formula)
}
