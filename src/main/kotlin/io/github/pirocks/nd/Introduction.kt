package io.github.pirocks.nd

import io.github.pirocks.logic.*


interface NDIntroductionStatement : NDStatement

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

class AndIntroduction(val left: NDStatement, val right: NDStatement) : NDIntroductionStatement {
    override val proves: And
        get() = And(left.proves,right.proves)

    override fun verify(given: Set<FOLFormula>): Boolean {
        return left.proves in given && right.proves in given
    }
}

class OrIntroductionLeft(val left: NDStatement, val right: FOLFormula) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return left.proves in given
    }

    override val proves: Or
        get() = Or(left.proves,right)
}

class OrIntroductionRight(val left: FOLFormula, val right: NDStatement) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return right.proves in given
    }

    override val proves: Or
        get() = Or(left,right.proves)
}

class ImpliesIntroduction(val intros: List<NDStatement>) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        val assumption: AssumptionStatement = intros.firstOrNull() as? AssumptionStatement ?: return false
        val result = intros.lastOrNull() ?: return false
        //todo recurse down

        return true
    }

    override val proves: Implies
        get() = Implies(intros.first().proves, intros.last().proves)
}

class AssumptionStatement(val assumption: FOLFormula) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return true
    }

    override val proves: FOLFormula
        get() = assumption
}


//class IFFIntroduction() : NDIntroductionStatement {
//}

class NegationIntroduction(val intros: List<NDStatement>) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        val proves = (intros.firstOrNull()?.proves as? Not ?: return false).child
        //todo recurse
        return intros.lastOrNull()?.proves is False
    }

    override val proves: FOLFormula
        get() = (intros.first().proves as Not).child
}

class TruthIntroduction : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return true
    }

    override val proves: True
        get() = True()
}

class FalseIntroduction(val contradictoryLeft: NDStatement, val contradictoryRight: NDStatement) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return (contradictoryLeft.proves as? Not)?.child?.let {
            it == contradictoryRight.proves
        } ?: (contradictoryRight.proves as? Not)?.child?.let {
            it == contradictoryLeft.proves
        } ?: false
    }

    override val proves: False
        get() = False()
}


/**
 * effectively copies a statement. needed for completeness reasons
 */
class IDIntroduction(val toCopy: NDStatement) : NDIntroductionStatement {
    override fun verify(given: Set<FOLFormula>): Boolean {
        return true//todo
    }

    override val proves: FOLFormula
        get() = toCopy.proves
}