package io.github.pirocks.nd

import io.github.pirocks.logic.*


abstract class NDEliminationStatement : NDStatementBase()

//class ForAllElimination(override val eliminationTarget: NDStatement, val from: VariableName, val to: VariableName) : NDEliminationStatement() {
//    override val value: FOLFormula
//        get() {
//            val targetWithoutForAll = (eliminationTarget.value as ForAll).child;
//            return renameVar(targetWithoutForAll, from, to)
//        }
//}
//
///**todo
// * add assumption var/interface, can also allow for customizing of rendering
// */
//class ExistsElimination(override val eliminationTarget: NDStatement, val skolemConstant: UUID, val children: List<NDStatement>) : NDEliminationStatement() {
//    override val value: FOLFormula
//        get() = children.last().value
//}

class AndEliminationLeft(val target: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(target)
    }

    override val proves: FOLFormula = (target.proves as And).left
}

class AndEliminationRight(val target: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(target)
    }

    override val proves: FOLFormula = (target.proves as And).left
}

class OrElimination(val target: NDStatement, val left: NDStatement, val right: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        val leftInputOr = (target.proves as? Or)?.left
        val rightInputOr = (target.proves as? Or)?.right
        val leftOutputOr = (left.proves as? Implies)?.result
        val rightOutputOr = (right.proves as? Implies)?.result
        val rightInputImplies = (right.proves as? Implies)?.given
        val leftInputImplies = (left.proves as? Implies)?.given
        val leftOutputImplies = (left.proves as? Implies)?.result
        val rightOutputImplies = (right.proves as? Implies)?.result
        val allValues = listOf(leftInputOr, rightInputOr, leftOutputOr, rightOutputOr, rightInputImplies, leftInputImplies, leftOutputImplies, rightOutputImplies)
        return allValues.all { it != null } &&
                leftOutputOr == rightOutputOr &&
                leftInputImplies == leftInputOr &&
                rightInputImplies == rightInputOr &&
                leftOutputImplies == leftOutputOr &&
                rightOutputImplies == rightOutputOr
    }

    override val proves: FOLFormula
        get() = Or(left.proves, right.proves)
}

/**
 * elimination target is statement 1:
 * 1) a
 * 2) a->b
 */
class ImpliesElimination(val eliminationTarget: NDStatement, val impliesStatement: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return (impliesStatement.proves as? Implies)?.left == eliminationTarget &&
                context.known(impliesStatement) &&
                context.known(eliminationTarget)
    }

    override val proves: FOLFormula
        get() = (impliesStatement.proves as Implies).left
}


/**
 * elimination target is statement 1:
 * 1) a
 * 2) a<->b
 */
class IFFEliminationLeft(val eliminationTarget: NDStatement, val iffStatement: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return eliminationTarget == (iffStatement.proves as IFF).left && context.known(eliminationTarget) && context.known(iffStatement)
    }

    override val proves: FOLFormula
        get() = (iffStatement.proves as IFF).right

}

/**
 * elimination target is statement 2:
 * 1) b
 * 2) a<->b
 */
class IFFEliminationRight(val eliminationTarget: NDStatement, val iffStatement: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return eliminationTarget == (iffStatement.proves as IFF).right &&
                context.known(eliminationTarget) &&
                context.known(iffStatement)
    }

    override val proves: FOLFormula
        get() = (iffStatement.proves as IFF).left

}

class DoubleNegationElimination(val eliminationTarget: NDStatement) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return (eliminationTarget.proves as? Not)?.child is Not && context.known(eliminationTarget)
    }

    override val proves: FOLFormula
        get() = ((eliminationTarget.proves as Not).child as Not).child
}

class FalsityElimination(val eliminationTarget: NDStatement, val value: FOLFormula) : NDEliminationStatement() {
    override fun verify(context: VerifierContext): Boolean {
        return eliminationTarget.proves == False() && context.known(eliminationTarget)
    }

    override val proves: FOLFormula
        get() = value
}