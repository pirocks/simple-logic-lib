package uk.ac.ic.doc.fpn17.nd

import uk.ac.ic.doc.fpn17.logic.*
import java.io.Serializable

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
}

interface NDSolver {
    val problem: Problem
    fun solve(): NDProof
}

class GivenStatement(override val value: FOLFormula) : NDStatement
class AssumptionStatement(override val value: FOLFormula) : NDStatement
class UnknownStatement(override val value: FOLFormula) : NDStatement//filler statement for init//todo really this should be abstract and have an equivalent version for each rule, as to allow for skeleton construction

//class NegationElimination(override val eliminationTarget: NDStatement) : NDEliminationStatement //covered by falsity introduction
//class TruthElimination : NDEliminationStatement//nop

class BruteForceSolver(override val problem: Problem) : NDSolver {
    override fun solve(): NDProof {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}