package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import java.util.*

class VerifierContext(private val alreadyVerified: MutableSet<NDStatement>) {
    private val toRemoveStack: Stack<MutableSet<NDStatement>> = Stack()

    fun known(ndStatement: NDStatement): Boolean {
        return ndStatement in alreadyVerified
    }

    fun assume(assumption: AssumptionStatement) {
        alreadyVerified.add(assumption)
        toRemoveStack.peek().add(assumption)
    }

    fun push() {
        toRemoveStack.push(mutableSetOf())
    }

    fun pop() {
        val toRemove = toRemoveStack.pop()
        alreadyVerified.removeAll(toRemove)
    }

    fun verify(ndStatement: NDStatement): Boolean {
        val res = ndStatement.verify(this)
        alreadyVerified.add(ndStatement)
        toRemoveStack.peek().add(ndStatement)
        return res
    }
}

class NDProof(val statements: List<NDStatement>, val given: Set<FOLFormula>, val result: FOLFormula) {
    fun verify(): Boolean {
        val context = VerifierContext(mutableSetOf())
        context.push()
        return statements.all { it.verify(context) }
                && statements.lastOrNull()?.proves == result
    }
}

interface NDStatement {
    fun verify(context: VerifierContext): Boolean
    val proves: FOLFormula
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

abstract class NDStatementBase : NDStatement {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != this.javaClass) return false
        if (other !is NDStatementBase) {
            assert(false)
            return false
        }
        return other.proves == proves
    }

    override fun hashCode(): Int = proves.hashCode()
}