package io.github.pirocks.nd

import io.github.pirocks.logic.FOLFormula
import java.io.Serializable
import java.util.*

data class Problem(val givens: Set<FOLFormula>, val toProve: FOLFormula)

interface NDStatement : Serializable {
    val proves: FOLFormula
    val uses : Set<NDStatement>
    val scope: Scope
}

interface NDEliminationStatement : NDStatement {
    val eliminationTarget: NDStatement
}
interface NDIntroductionStatement : NDStatement

class NDProof(override val uses: Set<NDStatement>) : NDStatement{
    override val scope: Scope
        get() = baseScope
    override val proves: FOLFormula
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    val baseScope = Scope(emptyList(),this)

    open fun verify(): Boolean = TODO()

}
class Scope internal constructor(val location : List<Int>, internal val proof: NDProof){
    val subScopes: List<Scope> = mutableListOf()
    fun isAccessible(toCheck:Scope): Boolean {
        return toCheck.location.zip(location).all {(toCheckSubScope,thisSubscope)-> toCheckSubScope == thisSubscope }//todo check
    }
    companion object {
        fun getNewChildScope(scope: Scope) {

            scope.proof.scope
        }

    }

}

interface NDSolver {
    val problem: Problem
    fun solve(): NDProof
}

class GivenStatement(override val proves: FOLFormula) : NDStatement {
    override val uses: Set<NDStatement>
        get() = emptySet()
}

class AssumptionStatement(override val proves: FOLFormula) : NDStatement {
    override val uses: Set<NDStatement>
        get() = emptySet()
}

data class Known(val inProof:Boolean, val statement: NDStatement)

class Knowns(val knowns : Set<Known>, var pastKnowns:Optional<Knowns> = Optional.empty())

class BruteForceSolver(override val problem: Problem) : NDSolver {

    /**
     * overview of implementation:
     * work backwards from goal, goal must have been derived by one of the introduction/elimination rules:
     * top candidate rules are:
     * double negation elimination -- last step of a proof by contradiction.
     * for all elimination
     * introduction rule for top level logical operator
     * falsity elimination
     * or elimination
     *
     * much less likely:
     * and elimination - only way this happens is if result is in givens - which is trivial to check for in beggining
     * implies elimination. also only happens if result in givens, check for at begining.
     *
     *
     */
    override fun solve(): NDProof {
//        val proof = UnknownBlockStatement();
//        problem.givens.forEach { proof.children.add(GivenStatement(it)) }
//        val givensAsKnowns = mutableListOf<Known>()
//        proof.children.forEach { givensAsKnowns.add(Known(true, it)) }
        TODO()
    }

    fun generateMoreKnowledge(knowns: Knowns, depth:Int): Knowns {
        TODO()
    }

}