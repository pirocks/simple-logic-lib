package io.github.pirocks.nd

import io.github.pirocks.logic.*


abstract class NDIntroductionStatement : NDStatementBase()

///**
// * A for all introduction has a forall const leading to a conclusion. end result removes forall cconst and replaces with general
// * statement
// */
//class ForAllIntroduction(val forAllVar : VariableName, val body: List<NDStatement>) : NDIntroductionStatement {
//    override fun verify(given: Set<FOLFormula>): Boolean {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    init {
//        if(body.isEmpty()){
//            throw IllegalArgumentException("requires at least one statement in body")
//        }
//    }
//}

//class ExistsIntroduction() : NDIntroductionStatement {
//
//}

class AndIntroduction(val left: NDStatement, val right: NDStatement) : NDIntroductionStatement() {
    override val proves: FOLFormula = And(left.proves, right.proves)

    override fun verify(context: VerifierContext): Boolean {
        return context.known(left) && context.known(right)
    }
}

class OrIntroductionRight(val left: NDStatement, val right: FOLFormula) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(left)
    }

    override val proves: FOLFormula = Or(left.proves, right)
}

class OrIntroductionLeft(val left: FOLFormula, val right: NDStatement) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(right)
    }

    override val proves: FOLFormula = Or(left, right.proves)
}

class ImpliesIntroduction(val assumption: AssumptionStatement, val steps: List<NDStatement>) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        context.push()
        context.assume(assumption)
        return steps.all {
            it.verify(context)
        }.also { context.pop() }
    }

    override val proves: FOLFormula

    init {
        val result = steps.lastOrNull()?.proves
                ?: throw MalformedProofException("Empty proof body for implies introduction")
        proves = Implies(assumption.proves, result)
    }
}

class AssumptionStatement(val assumption: FOLFormula) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return false//Assumption statements should always be skipped when added correctly via Implies/NotIntro.
    }

    override val proves: FOLFormula = assumption
}


class IFFIntroduction(val leftImplies: NDStatement, val rightImplies: NDStatement) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return verifyMatching() &&
                context.known(leftImplies) &&
                context.known(rightImplies)
    }

    private fun verifyMatching(): Boolean {
        val leftGiven = (leftImplies.proves as? Implies)?.given ?: return false
        val rightGiven = (rightImplies.proves as? Implies)?.given ?: return false
        val leftResult = (leftImplies.proves as? Implies)?.result ?: return false
        val rightResult = (rightImplies.proves as? Implies)?.result ?: return false
        return leftGiven == rightResult &&
                leftResult == rightGiven
    }

    override val proves: FOLFormula

    init {
        if (!verifyMatching()) throw MalformedProofException("IFF intro incompatible left and right implications ")
        val leftOfIFF = (leftImplies.proves as Implies).given
        val rightOfIFF = (leftImplies.proves as Implies).result
        proves = IFF(leftOfIFF, rightOfIFF)
    }
}

class NegationIntroduction(val assumption: AssumptionStatement, val steps: List<NDStatement>) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        context.push()
        context.assume(assumption)
        return steps.all {
            context.verify(it)
        }.also { context.pop() } && steps.lastOrNull()?.proves is False
    }

    override val proves: FOLFormula

    init {
        if (steps.isEmpty()) {
            throw MalformedProofException("Negation introduction received empty proof body")
        }
        proves = Not(assumption.proves)
    }
}

class TruthIntroduction : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return true
    }

    override val proves: FOLFormula = True()
}

class FalseIntroduction(val contradictoryLeft: NDStatement, val contradictoryRight: NDStatement) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return (contradictoryLeft.proves as? Not)?.child?.let {
            it == contradictoryRight.proves
        } ?: (contradictoryRight.proves as? Not)?.child?.let {
            it == contradictoryLeft.proves
        } ?: false && context.known(contradictoryLeft) && context.known(contradictoryRight)
    }

    override val proves: FOLFormula = False()
}


/**
 * effectively copies a statement. needed for completeness reasons
 */
class IDIntroduction(val toCopy: NDStatement) : NDIntroductionStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(toCopy)
    }

    override val proves: FOLFormula = toCopy.proves
}