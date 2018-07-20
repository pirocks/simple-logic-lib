package uk.ac.ic.doc.fpn17.nd

import uk.ac.ic.doc.fpn17.logic.*
import java.io.Serializable
import java.util.*

data class Problem(val givens: Set<FOLFormula>, val toProve: FOLFormula)

@Suppress("RedundantModalityModifier")
interface NDStatement : Serializable {
    abstract val value: FOLFormula
}

@Suppress("RedundantModalityModifier")
interface NDEliminationStatement : NDStatement{
    abstract val eliminationTarget: NDStatement
}
interface NDIntroductionStatement : NDStatement

interface NDProof {
    val statement: List<NDStatement>
    fun verify()
    fun isCompleted():True
}

interface NDSolver {
    val problem: Problem
    fun solve(): NDProof
}

class GivenStatement(override val value: FOLFormula) : NDStatement
class AssumptionStatement(override val value: FOLFormula) : NDStatement
class UnknownBlockStatement() : BlockStatement(listOf()){
    override val children = LinkedList<NDStatement>()

}//filler statement for init//todo really this should be abstract and have an equivalent version for each rule, as to allow for skeleton construction
open class BlockStatement(open val children: List<NDStatement>) :NDStatement {
    override val value: FOLFormula
        get() = children.last().value
}
//class NegationElimination(override val eliminationTarget: NDStatement) : NDEliminationStatement //covered by falsity introduction
//class TruthElimination : NDEliminationStatement//nop

data class Known(val inProof:Boolean, val statement: NDStatement)

class Knowns(val knowns : Set<Known>,var pastKnowns:Optional<Knowns> = Optional.empty())

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
        val proof = UnknownBlockStatement();
        problem.givens.forEach { proof.children.add(GivenStatement(it)) }
        val givensAsKnowns = mutableListOf<Known>()
        proof.children.forEach { givensAsKnowns.add(Known(true,it)) }

    }

    fun generateMoreKnowledge(knowns: Knowns,depth:Int):Knowns{

    }

}