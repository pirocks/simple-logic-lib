package uk.ac.ic.doc.fpn17.nd

import uk.ac.ic.doc.fpn17.logic.*
import java.util.*


class ForAllElimination(override val eliminationTarget: NDStatement, val fromUUID: UUID, val toUUID: UUID) : NDEliminationStatement {
    override val value: FOLFormula
        get() {
            val targetWithoutForAll = (eliminationTarget.value as ForAll).child;
            return renameVar(targetWithoutForAll,fromUUID,toUUID)
        }
}

/**todo
 * add assumption var/interface, can also allow for customizing of rendering
 */
class ExistsElimination(override val eliminationTarget: NDStatement, val skolemConstant: UUID, val children: List<NDStatement>) : NDEliminationStatement {
    override val value: FOLFormula
        get() = children.last().value
}

class AndEliminationLeft(override val eliminationTarget: NDStatement, val andStatement: NDStatement) : NDEliminationStatement {
    override val value: FOLFormula
        get() = (andStatement.value as And).left
}

class AndEliminationRight(override val eliminationTarget: NDStatement, val andStatement: NDStatement) : NDEliminationStatement {
    override val value: FOLFormula
        get() = (andStatement.value as And).right
}

class OrElimination(override val eliminationTarget: NDStatement, val childrenLeft: List<NDStatement>, val childrenRight: List<NDStatement>, val orStatement: NDIntroductionStatement) : NDEliminationStatement {
    init {
        assert(childrenLeft.last() == childrenRight.last())
    }
    override val value: FOLFormula
        get() = childrenLeft.last().value
}

/**
 * elimination target is statement 1:
 * 1) a
 * 2) a->b
 */
class ImpliesElimination(override val eliminationTarget: NDStatement, val impliesStatement: NDStatement) : NDEliminationStatement {
    init{
        assert(impliesStatement.value is Implies)
        assert((impliesStatement.value as Implies).given == eliminationTarget.value);
    }
    override val value: FOLFormula
        get() = (impliesStatement.value as Implies).result
}

/**
 * elimination target is statement 1:
 * 1) a
 * 2) a<->b
 */
class IFFEliminationOne(override val eliminationTarget: NDStatement, val iffStatement: NDStatement) : NDEliminationStatement {
    init{
        assert(iffStatement.value is IFF)
        assert((iffStatement.value as IFF).one == eliminationTarget.value);
    }
    override val value: FOLFormula
        get() = (iffStatement.value as IFF).two
}

/**
 * elimination target is statement 2:
 * 1) b
 * 2) a<->b
 */
class IFFEliminationTwo(override val eliminationTarget: NDStatement, val iffStatement: NDStatement) : NDEliminationStatement {
    init{
        assert(iffStatement.value is IFF)
        assert((iffStatement.value as IFF).two == eliminationTarget.value);
    }
    override val value: FOLFormula
        get() = (iffStatement.value as IFF).one
}

class DoubleNegationElimination(override val eliminationTarget: NDStatement): NDEliminationStatement {
    init {
        assert(eliminationTarget.value is Negation)
        assert((eliminationTarget.value as Negation).child is Negation)
    }
    override val value: FOLFormula
        get() = ((eliminationTarget.value as Negation).child as Negation).child
}

class FalsityElimination(override val eliminationTarget: NDStatement, override val value: FOLFormula) : NDEliminationStatement{
    init {
        assert (eliminationTarget.value is False)
    }
}