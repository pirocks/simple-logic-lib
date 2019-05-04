package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import io.github.pirocks.provers.Proof
import java.io.Serializable
import java.util.*

//todo could be faster b/c iterating through stack
class VerifierContext(private val alreadyVerified: Stack<MutableSet<NDStatement>>) {

    fun known(ndStatement: NDStatement): Boolean {
        return alreadyVerified.any { ndStatement in it }
    }

    fun assume(assumption: AssumptionStatement) {
        alreadyVerified.peek().add(assumption)
    }

    fun push() {
        alreadyVerified.push(mutableSetOf())
    }

    fun pop() {
        alreadyVerified.pop()
    }

    fun verify(ndStatement: NDStatement): Boolean {
        val res = ndStatement.verify(this)
        alreadyVerified.peek().add(ndStatement)
        return res
    }
}

class MalformedProofException(msg: String) : Exception(msg)

class NDProof(val statements: List<NDStatement>, val given: Set<GivenStatement>, val result: FOLFormula) : Serializable, Proof {
    fun verify(): Boolean {
        val stack = Stack<MutableSet<NDStatement>>()
        stack.push(given.toMutableSet())
        val context = VerifierContext(stack)
        return statements.all { context.verify(it) }
                && statements.lastOrNull()?.proves == result
    }
}

interface NDStatement : Serializable{
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

class GivenStatement(val given: FOLFormula) : NDStatementBase() {
    override fun verify(context: VerifierContext): Boolean {
        return context.known(this)
    }

    override val proves: FOLFormula = given
}