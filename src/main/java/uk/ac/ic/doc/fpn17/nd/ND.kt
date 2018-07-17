package uk.ac.ic.doc.fpn17.nd

import java.io.Serializable
import java.util.*

data class Problem(val givens: Set<FOLFormula>, val toProve: FOLFormula)

interface NDStatement : Serializable{
    abstract val value: FOLFormula
}
interface NDEliminationStatement : NDStatement
interface NDIntroductionStatement : NDStatement

interface NDProof{
    val statement: List<NDStatement>
    fun verify()
}

interface NDSolver{
    val problem: Problem
    fun solve():NDProof
}

class GivenStatement(override val value: FOLFormula) : NDStatement
class AssumptionStatement(override val value: FOLFormula) : NDStatement
class UnknownStatement(override val value: FOLFormula) : NDStatement//filler statement for init
class ForAllIntroduction(val varUUID:UUID,val children:List<NDStatement>) : NDIntroductionStatement
class ExistsIntroduction(val varUUID:UUID,val children:List<NDStatement>) : NDIntroductionStatement
class AndIntroduction(val left: NDStatement, val right: NDStatement) : NDIntroductionStatement
class OrIntroductionLeft(val left: NDStatement, val right: FOLFormula) : NDIntroductionStatement
class OrIntroductionRight(val left: FOLFormula, val right: NDStatement) : NDIntroductionStatement
class ImpliesIntroduction(val assumption: FOLFormula, val result: FOLFormula, val children: List<NDStatement>) : NDIntroductionStatement
class IFFIntroduction(val one: NDStatement, val two: NDStatement) : NDIntroductionStatement
class NegationIntroduction(val children: List<NDStatement>) : NDIntroductionStatement
class TruthIntroduction : NDIntroductionStatement {
    override val value: FOLFormula
        get() =
}

class FalsityIntroduction : NDIntroductionStatement
class ForAllElimination : NDEliminationStatement
class ExistsElimination : NDEliminationStatement
class AndElimination : NDEliminationStatement
class OrElimination : NDEliminationStatement
class ImpliesElimination : NDEliminationStatement
class IFFElimination : NDEliminationStatement
class NegationElimination : NDEliminationStatement
class TruthElimination : NDEliminationStatement
class FalsityElimination : NDEliminationStatement

class BruteForceSolver(override val problem: Problem) : NDSolver{
    override fun solve(): NDProof {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}