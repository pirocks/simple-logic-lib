package uk.ac.ic.doc.fpn17.nd

import uk.ac.ic.doc.fpn17.logic.*
import java.util.*

/**
 * A for all introduction has a forall const leading to a conclusion. end result removes forall cconst and replaces with general
 * statement
 */
class ForAllIntroduction(val forAllConst:UUID,val desiredForAllvarUUID: UUID, val children: List<NDStatement>) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = ForAll(renameVar(children.last().value,forAllConst,desiredForAllvarUUID), desiredForAllvarUUID)
}

/**
 * A exists introduction takes a instance of a formula and turns it into exists etc...
 * @param existsVarCurrent the variable that will be replaced with an exists
 * @param desiredExistsvarUUID desired uuid for exists bound var
 */
class ExistsIntroduction(val existsVarCurrent: UUID,val desiredExistsvarUUID: UUID, val targetStatement: NDStatement) : NDIntroductionStatement {
    override val value: FOLFormula
        get() = Exists(renameVar(targetStatement.value,existsVarCurrent,desiredExistsvarUUID), desiredExistsvarUUID)
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

/**
 * todo wrong
 */
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