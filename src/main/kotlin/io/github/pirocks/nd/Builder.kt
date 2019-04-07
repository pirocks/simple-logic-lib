package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import io.github.pirocks.logic.VariableName

fun proof(result: FOLFormula, givens: Set<FOLFormula> = emptySet(), verify: Boolean = false, init: MutableList<NDStatement>.() -> Unit): NDProof {
    val statements = mutableListOf<NDStatement>()
    statements.init()
    val res = NDProof(statements, givens.map { GivenStatement(it) }.toMutableSet(), result)
    if (verify && !res.verify()) {
        throw MalformedProofException("Proof failed to verify")
    }
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

fun MutableList<NDStatement>.trueIntro(): TruthIntroduction = TruthIntroduction().also { add(it) }


fun MutableList<NDStatement>.andIntro(left: NDStatement, right: NDStatement): AndIntroduction = AndIntroduction(left, right).also { add(it) }


fun MutableList<NDStatement>.andElimLeft(target: NDStatement): AndEliminationLeft = AndEliminationLeft(target).also { add(it) }


fun MutableList<NDStatement>.andElimRight(target: NDStatement): AndEliminationRight = AndEliminationRight(target).also { add(it) }


fun MutableList<NDStatement>.orElim(target: NDStatement, left: NDStatement, right: NDStatement): OrElimination = OrElimination(target, left, right).also { add(it) }


fun MutableList<NDStatement>.impliesElim(eliminationTarget: NDStatement, impliesStatement: NDStatement): ImpliesElimination = ImpliesElimination(eliminationTarget, impliesStatement).also { add(it) }


fun MutableList<NDStatement>.iffElimLeft(eliminationTarget: NDStatement, iffStatement: NDStatement): IFFEliminationLeft = IFFEliminationLeft(eliminationTarget, iffStatement).also { add(it) }


fun MutableList<NDStatement>.iffElimRight(eliminationTarget: NDStatement, iffStatement: NDStatement): IFFEliminationRight = IFFEliminationRight(eliminationTarget, iffStatement).also { add(it) }


fun MutableList<NDStatement>.doubleNegElim(eliminationTarget: NDStatement): DoubleNegationElimination = DoubleNegationElimination(eliminationTarget).also { add(it) }


fun MutableList<NDStatement>.falsityElim(eliminationTarget: NDStatement, to: FOLFormula): FalsityElimination = FalsityElimination(eliminationTarget, to).also { add(it) }


fun MutableList<NDStatement>.orIntro(left: FOLFormula, right: NDStatement): OrIntroductionLeft = OrIntroductionLeft(left, right).also { add(it) }


fun MutableList<NDStatement>.orIntro(left: NDStatement, right: FOLFormula): OrIntroductionRight = OrIntroductionRight(left, right).also { add(it) }


fun MutableList<NDStatement>.iFFIntro(leftImplication: NDStatement, rightImplication: NDStatement): IFFIntroduction = IFFIntroduction(leftImplication, rightImplication).also { add(it) }


fun MutableList<NDStatement>.negationIntro(assumptionStatement: AssumptionStatement, init: MutableList<NDStatement>.(assumption: NDStatement) -> Unit): NegationIntroduction {
    val statements = mutableListOf<NDStatement>()
    statements.init(assumptionStatement)
    val implication = NegationIntroduction(assumptionStatement, statements)
    add(implication)
    return implication
}

fun MutableList<NDStatement>.falseIntro(contradictoryLeft: NDStatement, contradictoryRight: NDStatement): FalseIntroduction =
        FalseIntroduction(contradictoryLeft, contradictoryRight).also { add(it) }

fun MutableList<NDStatement>.idIntro(toCopy: NDStatement): IDIntroduction = IDIntroduction(toCopy).also { add(it) }

fun assume(formula: FOLFormula): AssumptionStatement {
    return AssumptionStatement(formula)
}

fun MutableList<NDStatement>.forAllIntro(init: MutableList<NDStatement>.(forAllConst: VariableName) -> Unit): ForAllIntroduction {
    val statements = mutableListOf<NDStatement>()
    val forAllConst = VariableName()
    statements.init(forAllConst)
    val elem = ForAllIntroduction(forAllConst, statements)
    add(elem)
    return elem
}

fun MutableList<NDStatement>.existsIntro(example: NDStatement, varToTarget: VariableName): ExistsIntroduction =
        ExistsIntroduction(example, varToTarget).also { add(it) }

fun MutableList<NDStatement>.existsElim(target: NDStatement, init: MutableList<NDStatement>.(skolemCons: VariableName, skolemConstExpr: AssumptionStatement) -> Unit): ExistsElimination {
    val statements = mutableListOf<NDStatement>()
    val skolemConstant = VariableName()
    val skolemConstantExpression = ExistsElimination.calcSkolemConstantExpression(target, skolemConstant)
    statements.init(skolemConstant, skolemConstantExpression)
    val element = ExistsElimination(target, statements, skolemConstant, skolemConstantExpression)
    add(element)
    return element
}

fun MutableList<NDStatement>.forAllElim(target: NDStatement, to: VariableName): ForAllElimination =
        ForAllElimination(target, to).also { add(it) }

fun given(folFormula: FOLFormula): GivenStatement {
    return GivenStatement(folFormula)
}
