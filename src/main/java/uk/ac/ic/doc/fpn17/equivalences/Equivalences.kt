package uk.ac.ic.doc.fpn17.equivalences

import uk.ac.ic.doc.fpn17.logic.*
import java.util.*


interface Equivalence{
    fun matches(formula:FOLFormula):Int

    fun apply(formula: FOLFormula, index:Int):FOLFormula
}

//there will be one equivalence for left direction, and one for right
abstract class EquivalenceImpl : Equivalence{
    abstract val patternFrom:FOLFormula;
    abstract val patternTo:FOLFormula;

    private fun matchesImpl(formula: FOLFormula,pattern:FOLFormula): Boolean {
        var res: Int = 0;
        object : RewritingVisitor(){

        }
        TODO()
    }
}

/*
equivalences to implement:
and:
(a & b,b & a)
(a & a,a)
(a & True,a)
(a & False,a)
(a & ~a,False)
(a & (b & c),(a & b) & c)
or:
(a | b, b | a)
iff:

 */