package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import io.github.pirocks.logic.True

fun proof(givens: Set<FOLFormula> = emptySet(), result: FOLFormula, init: MutableList<NDStatement>.() -> Unit): NDProof {
    val statements = mutableListOf<NDStatement>()
    statements.init()
    val res = NDProof(statements, givens, result)
    return res
}

fun MutableList<NDStatement>.implies(assumptionStatement: AssumptionStatement, init: MutableList<NDStatement>.(assumption: NDStatement) -> Unit): ImpliesIntroduction {
    val statements = mutableListOf<NDStatement>()
    statements.init(assumptionStatement)
    assert(statements.isNotEmpty())
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

fun MutableList<NDStatement>.andElimLeft(target: NDStatement): AndEliminationLeft {
    val element = AndEliminationLeft(target)
    add(element)
    return element
}

fun MutableList<NDStatement>.andElimRight(target: NDStatement): AndEliminationRight {
    val element = AndEliminationRight(target)
    add(target)
    return element
}

fun MutableList<NDStatement>.orElim(target: NDStatement, left: NDStatement, right: NDStatement): OrElimination {
    val element = OrElimination(target, left, right)
    add(target)
    return element
}

fun MutableList<NDStatement>.impliesElim(eliminationTarget: NDStatement, impliesStatement: NDStatement): ImpliesElimination {
    val element = ImpliesElimination(eliminationTarget, impliesStatement)
    add(element)
    return element
}

fun MutableList<NDStatement>.iffElimLeft(eliminationTarget: NDStatement, iffStatement: NDStatement): IFFEliminationLeft {
    val element = IFFEliminationLeft(eliminationTarget, iffStatement)
    add(element)
    return element
}

fun MutableList<NDStatement>.iffElimRight(eliminationTarget: NDStatement, iffStatement: NDStatement): IFFEliminationRight {
    val element = IFFEliminationRight(eliminationTarget, iffStatement)
    add(element)
    return element
}

fun MutableList<NDStatement>.doubleNegElim(eliminationTarget: NDStatement): DoubleNegationElimination {
    val element = DoubleNegationElimination(eliminationTarget)
    add(element)
    return element
}

fun MutableList<NDStatement>.falsityElim(eliminationTarget: NDStatement, to: FOLFormula): FalsityElimination {
    val element = FalsityElimination(eliminationTarget, to)
    add(element)
    return element
}
//fun MutableList<NDStatement>.forAllIntro(){
//
//}
//fun MutableList<NDStatement>.existsIntro(){
//
//}
fun MutableList<NDStatement>.orIntro(left: FOLFormula, right: NDStatement): OrIntroductionLeft {
    val element = OrIntroductionLeft(left, right)
    add(element)
    return element
}

fun MutableList<NDStatement>.orIntro(left: NDStatement, right: FOLFormula): OrIntroductionRight {
    val element = OrIntroductionRight(left, right)
    add(element)
    return element
}

fun MutableList<NDStatement>.iFFIntro(leftImplication: NDStatement, rightImplication: NDStatement): IFFIntroduction {
    val element = IFFIntroduction(leftImplication, rightImplication)
    add(element)
    return element
}

fun MutableList<NDStatement>.negationIntro(assumptionStatement: AssumptionStatement, init: MutableList<NDStatement>.(assumption: NDStatement) -> Unit): NegationIntroduction {
    val statements = mutableListOf<NDStatement>()
    statements.init(assumptionStatement)
    val implication = NegationIntroduction(assumptionStatement, statements)
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
