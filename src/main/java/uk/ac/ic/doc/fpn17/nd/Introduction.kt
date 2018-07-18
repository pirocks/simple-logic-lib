package uk.ac.ic.doc.fpn17.nd

import uk.ac.ic.doc.fpn17.logic.*
import java.util.*

class ForAllIntroduction(val varUUID: UUID, val children: List<NDStatement>, val targetStatementWithoutQuantifier: FOLFormula) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = ForAll(targetStatementWithoutQuantifier, varUUID)

    init {
        //todo this isn't really the right place:
        assert(children.last().value == targetStatementWithoutQuantifier)
    }
}

class ExistsIntroduction(val varUUID: UUID, val children: List<NDStatement>, val targetStatementWithoutQuantifier: FOLFormula) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Exists(targetStatementWithoutQuantifier, varUUID)

    init {
        //todo this isn't really the right place:
        assert(children.last().value == targetStatementWithoutQuantifier)
    }
}

class AndIntroduction(val left: NDStatement, val right: NDStatement) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = And(left.value, right.value)
}

class OrIntroductionLeft(val left: NDStatement, val right: FOLFormula) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Or(left.value, right)
}

class OrIntroductionRight(val left: FOLFormula, val right: NDStatement) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Or(left, right.value)
}

class ImpliesIntroduction(val assumption: FOLFormula, val result: FOLFormula, val children: List<NDStatement>) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Implies(assumption, result)
}

class IFFIntroduction(val one: NDStatement, val two: NDStatement) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = IFF(one.value, two.value)
}

class NegationIntroduction(val children: List<NDStatement>, val targetStatementWithoutNegation: FOLFormula) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Negation(targetStatementWithoutNegation)
}

class TruthIntroduction : NDIntroductionStatement {
    override val value: FOLFormula
        get() = True()
}

class FalsityIntroduction(val contradictoryOne: NDStatement, val contradictoryTwo: NDStatement) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = False()

    init {
        if (contradictoryOne.value is Negation) {
            (contradictoryOne.value as Negation).child == contradictoryTwo
        } else if (contradictoryTwo.value is Negation) {
            (contradictoryTwo.value as Negation).child == contradictoryOne
        } else {
            assert(false);
        }
    }

}